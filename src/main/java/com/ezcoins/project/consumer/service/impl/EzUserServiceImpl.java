package com.ezcoins.project.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.LimitType;
import com.ezcoins.constant.enums.LoginType;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.exception.CheckException;
import com.ezcoins.exception.user.UserException;
import com.ezcoins.exception.user.UserPasswordNotMatchException;
import com.ezcoins.project.acl.entity.req.JwtAuthenticationRequest;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.common.mq.producer.LoginProducer;
import com.ezcoins.project.common.service.EmailService;
import com.ezcoins.project.common.service.PhoneService;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserLimit;
import com.ezcoins.project.consumer.entity.EzUserLimitLog;
import com.ezcoins.project.consumer.entity.req.CheckCodeReqDto;
import com.ezcoins.project.consumer.entity.req.EzUserReqDto;
import com.ezcoins.project.consumer.entity.req.VerificationCodeReqDto;
import com.ezcoins.project.consumer.mapper.EzUserMapper;
import com.ezcoins.project.consumer.service.EzUserLimitLogService;
import com.ezcoins.project.consumer.service.EzUserLimitService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.security.util.JWTHelper;
import com.ezcoins.security.util.JWTInfo;
import com.ezcoins.security.util.SecurityUtils;
import com.ezcoins.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-05-26
 */
@Service
@Slf4j
public class EzUserServiceImpl extends ServiceImpl<EzUserMapper, EzUser> implements EzUserService {
    @Autowired
    private EzUserMapper ezUserMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JWTHelper jwtHelper;

    @Autowired
    private LoginProducer loginProducer;


    @Autowired
    private EzAdvertisingBusinessService businessService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EzUserLimitLogService limitLogService;

    @Autowired
    private EzUserLimitService limitService;


    /**
     * 检查用户唯一性  不存在返回true
     *
     * @param userName
     * @param phone
     * @param email
     * @param inviteCode
     * @return
     */
    @Override
    public boolean checkUserUnique(String userName, String phone, String phoneArea, String email, String inviteCode) {
        return ezUserMapper.checkUserUnique(userName, phone, phoneArea, email, inviteCode) == 0;
    }

    /**
     * 获取验证码
     *
     * @param inviteCode
     * @return
     */
    @Override
    public String getInviteCode(String inviteCode) {
        if (checkUserUnique(null, null, null, null, inviteCode)) {
            return inviteCode;
        }
        return getInviteCode(RandomUtil.generateByRandom(10));
    }

    /**
     * 根据用户名/手机号查询用户
     *
     * @param userName
     * @param phone
     * @return
     */
    @Override
    public EzUser selectUserBy(String userName, String phone, String phoneArea, String email, String inviteCode) {
        return ezUserMapper.selectUserBy(userName, phone, phoneArea, email, inviteCode);
    }

    /**
     * 发送验证码
     *
     * @param codeReqDto
     */
    @Override
    public void sendMsm(VerificationCodeReqDto codeReqDto) {
        String type = codeReqDto.getType();
        String verificationNumber = codeReqDto.getVerificationNumber();
        String captchaType = codeReqDto.getCaptchaType();
        String phoneArea = codeReqDto.getPhoneArea();

        String key = null;
        String code = null;
        //手机验证码
        if (type.equals(VerificationCodeReqDto.Type.PHONE.getType())) {
            if (StringUtils.isEmpty(phoneArea)) {
                throw new BaseException("请选择国际区号");
            }
            if (VerificationCodeReqDto.captchaType.RETRIEVE_SECURITY_PASSWORD.getType().equals(captchaType)) {
                key = RedisConstants.PHONE_RETRIEVE_SECURITY_PWD;
            } else {
                boolean flag = checkUserUnique(null, verificationNumber, phoneArea, null, null);
                //注册验证吗
                if (VerificationCodeReqDto.captchaType.REGISTER.getType().equals(captchaType)) {
                    if (!flag) {
                        throw new BaseException("手机号已被注册");
                    }
                    key = RedisConstants.PHONE_REGISTER_SMS_KEY;
                } else if (VerificationCodeReqDto.captchaType.RETRIEVE_PASSWORD.getType().equals(captchaType)) {
                    if (flag) {
                        throw new BaseException("手机号尚未绑定");
                    }
                    key = RedisConstants.PHONE_RETRIEVE_PASSWORD_SMS_KEY;
                } else if (VerificationCodeReqDto.captchaType.BIND_INFO.getType().equals(captchaType)) {
                    if (!flag) {
                        throw new BaseException("手机号已被注册");
                    }
                    key = RedisConstants.PHONE_BIND_INFO_SMS_KEY;
                }
            }
            code = redisCache.getCacheObject(key + phoneArea + verificationNumber);
        } else if (type.equals(VerificationCodeReqDto.Type.EMAIL.getType())) {
            if (VerificationCodeReqDto.captchaType.RETRIEVE_SECURITY_PASSWORD.getType().equals(captchaType)) {
                key = RedisConstants.EMAIL_RETRIEVE_SECURITY_PWD;
            } else {
                boolean flag = checkUserUnique(null, null, null, verificationNumber, null);
                //注册验证吗
                if (VerificationCodeReqDto.captchaType.REGISTER.getType().equals(captchaType)) {
                    if (!flag) {
                        throw new BaseException("邮箱已被注册");
                    }
                    key = RedisConstants.EMAIL_REGISTER_SMS_KEY;
                } else if (VerificationCodeReqDto.captchaType.RETRIEVE_PASSWORD.getType().equals(captchaType)) {
                    if (flag) {
                        throw new BaseException("邮箱尚未绑定");
                    }
                    key = RedisConstants.EMAIL_RETRIEVE_PASSWORD_SMS_KEY;
                } else if (VerificationCodeReqDto.captchaType.BIND_INFO.getType().equals(captchaType)) {
                    if (!flag) {
                        throw new BaseException("邮箱已被注册");
                    }
                    key = RedisConstants.EMAIL_BIND_INFO_SMS_KEY;
                }
            }
            code = redisCache.getCacheObject(key + verificationNumber);
        }
        //1 从redis获取验证码，如果获取到直接返回
        if (StringUtils.isNotEmpty(code)) {
        } else {
            //随机产生6位数字
            code = RandomUtil.getSixBitRandom();
            if (type.equals(VerificationCodeReqDto.Type.PHONE.getType())) {
                phoneService.send(key, code, phoneArea + verificationNumber);
            } else if (type.equals(VerificationCodeReqDto.Type.EMAIL.getType())) {
                try {
                    emailService.sendComplexMail(key, verificationNumber, MessageUtils.message("感谢您使用亿智交易平台"), code);
                    log.info("发送邮件成功，邮箱为：{},邮箱验证码为：{}", verificationNumber, code);
                } catch (Exception e) {
                    log.info("发送邮件失败-异常", e);
                }
            }
        }
    }

    /**
     * 注册用户
     *
     * @param ezUserDto
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void addUser(EzUserReqDto ezUserDto, boolean isAdmin) {
        //获取注册的数据
        String phone = ezUserDto.getPhone();
        String phoneArea = ezUserDto.getPhoneArea();
        String email = ezUserDto.getEmail();
        String type = ezUserDto.getType();
        String password = ezUserDto.getPassword();
        String parentInviteCode = ezUserDto.getInviteCode();//父级邀请码
        String countryCode = ezUserDto.getCountryCode();//国级代码

        EzUser ezUser = new EzUser();
        //获取redis验证码
        String rmKey = null;
        if (type.equals(VerificationCodeReqDto.Type.PHONE.getType())) {
            if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(phoneArea)) {
                throw new BaseException("手机号不能为空");
            }
            //判断手机号用户名是否重复，表里面存在相同手机号不进行添加
            if (!checkUserUnique(null, phone, phoneArea, null, null)) {
                throw new BaseException("手机号码已被注册");
            }
            ezUser.setPhone(phone);
            ezUser.setPhoneArea(phoneArea);
            ezUser.setUserName(phone);
            ezUser.setCreateBy(phone);
            rmKey = RedisConstants.PHONE_REGISTER_SMS_KEY + phone;
        } else if (type.equals(VerificationCodeReqDto.Type.EMAIL.getType())) {
            //判断手机号用户名是否重复，表里面存在相同手机号不进行添加
            if (!checkUserUnique(null, null, null, email, null)) {
                throw new BaseException("邮箱已被注册");
            }
            ezUser.setUserName(email);
            ezUser.setEmail(email);
            ezUser.setCreateBy(email);
            rmKey = RedisConstants.EMAIL_REGISTER_SMS_KEY + email;
        }
        String redisCode = redisCache.getCacheObject(rmKey).toString();
        if (!isAdmin) {
            if (StringUtils.isEmpty(redisCode)) {
                throw new BaseException("验证码已过期");
            }
            String code = ezUserDto.getCode();
            if (!code.equals(redisCode)) {
                throw new BaseException("验证码错误");
            }
        }
        String parentId = "0";
        if (StringUtils.isNotEmpty(parentInviteCode)) {
            EzUser ezUser1 = baseMapper.selectUserBy(null, null, null, null, parentInviteCode);
            if (ezUser1 == null) {
                throw new BaseException("邀请码错误");
            }
            parentId = ezUser1.getParentId();
        }
        //通过邀请码查询人
        ezUser.setParentId(parentId);
        ezUser.setCountryCode(countryCode);
        //获得邀请码
        String inviteCode = getInviteCode(RandomUtil.generateByRandom(10));
        ezUser.setInviteCode(inviteCode);
        ezUser.setPassword(EncoderUtil.encode(password));
        baseMapper.insert(ezUser);
        //初始化OTC信息
        EzAdvertisingBusiness advertisingBusiness = new EzAdvertisingBusiness();
        advertisingBusiness.setAdvertisingName(ezUser.getUserName());
        advertisingBusiness.setUserId(ezUser.getUserId());
        advertisingBusiness.setCreateBy(ezUser.getUserName());
        businessService.save(advertisingBusiness);
        //初始化账户信息
        accountService.processCoinAccount(ezUser.getUserId(), ezUser.getUserName());
        redisCache.deleteObject(rmKey);
    }

    /**
     * 用户登录
     *
     * @param authenticationRequest
     * @return
     */
    @Override
    @Transactional(value="transactionManager1")
    public Map<String, String> login(JwtAuthenticationRequest authenticationRequest) {
        //登录的时候 如果绑定了邮箱/电话号码 都可以用来登录
        LambdaQueryWrapper<EzUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzUser::getPhone, authenticationRequest.getUsername()).or().eq(EzUser::getEmail, authenticationRequest.getUsername());
        EzUser ezUser = baseMapper.selectOne(lambdaQueryWrapper);
        //用户不存在
        if (ezUser == null) {
            throw new UserException("登录用户不存在", null);
        }
        LambdaQueryWrapper<EzUserLimitLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzUserLimitLog::getIsExpire, "0");
        queryWrapper.eq(EzUserLimitLog::getUserId, ezUser.getUserId());
        queryWrapper.eq(EzUserLimitLog::getType, LimitType.LOGINLIMIT.getCode());
        EzUserLimitLog one = limitLogService.getOne(queryWrapper);
        if (one != null) {
            if (one.getBanTime() != null && one.getBanTime().getTime() < DateUtils.getNowDate().getTime()) {
                one.setIsExpire("1");
                limitLogService.updateById(one);
                ezUser.setStatus("0");
                baseMapper.updateById(ezUser);
                LambdaUpdateWrapper<EzUserLimit> ezUserLimitLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                ezUserLimitLambdaUpdateWrapper.eq(EzUserLimit::getUserId, ezUser.getUserId());
                ezUserLimitLambdaUpdateWrapper.set(EzUserLimit::getLogin, 0);
                limitService.update(ezUserLimitLambdaUpdateWrapper);
            }else {
                throw new UserException("此账号已被封锁", null);
            }
        }
        //密码错误
        if (!EncoderUtil.matches(authenticationRequest.getPassword(), ezUser.getPassword())) {
            throw new UserPasswordNotMatchException();
        }
        //创建token
        JWTInfo jwtInfo = new JWTInfo(ezUser.getUserName(), ezUser.getUserId(), LoginType.APP.getType());
        String token = jwtHelper.createToken(jwtInfo);
        log.info("  require logging... >>userToken:{}<<", token);
        String userId = ezUser.getUserId();
        ezUser.setLoginDate(DateUtils.getNowDate());
        final String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        ezUser.setLoginIp(ip);
        baseMapper.updateById(ezUser);
        loginProducer.sendMsgLoginFollowUp(ezUser.getUserName(), userId, ezUser.getNickName(), LoginType.APP.getType());
        Map<String, String> map = new HashMap<>(2);
        map.put("token", token);
        map.put("userId", userId);
        return map;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateUser(EzUserReqDto ezUserDto) {
        //查询用户
        EzUser ezUser = baseMapper.selectById(ezUserDto.getUserId());
        CheckException.checkNull(ezUser, "用户不存在");
        //密码
        String password = ezUserDto.getPassword();
        CheckException.checkNotEmpty(password, () -> {
            ezUser.setPassword(EncoderUtil.encode(password));
        });
        //创建时间
        Date createTime = ezUserDto.getCreateTime();
        CheckException.checkNotNull(createTime, () -> {
            ezUser.setCreateTime(createTime);
        });
        ezUser.setUpdateBy(SecurityUtils.getUsername());
        ezUser.setUpdateTime(DateUtils.getNowDate());
        CheckException.checkDb(baseMapper.updateById(ezUser), "用户更新失败");
    }

    @Override
    public void checkCode(CheckCodeReqDto checkCodeReqDto) {
        String phone = checkCodeReqDto.getPhone();
        String phoneArea = checkCodeReqDto.getPhoneArea();
        String email = checkCodeReqDto.getEmail();
        String type = checkCodeReqDto.getType();
        String captchaType = checkCodeReqDto.getCaptchaType();

        String redisCode = null;
        String key=null;
        if (type.equals(VerificationCodeReqDto.Type.PHONE.getType())) {
            if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(phoneArea)) {
                throw new BaseException("手机号不能为空");
            }
            //判断手机号用户名是否重复，表里面存在相同手机号不进行添加
            if (!checkUserUnique(null, phone, phoneArea, null, null)) {
                throw new BaseException("手机号码已被注册");
            }
            key=RedisConstants.PHONE_REGISTER_SMS_KEY+phoneArea + phone;
        } else if (type.equals(VerificationCodeReqDto.Type.EMAIL.getType())) {
            boolean flag = checkUserUnique(null, null, null, email, null);
            if (captchaType.equals(VerificationCodeReqDto.captchaType.REGISTER.getType())){
                if (!flag){
                    throw new BaseException("邮箱已被注册");
                }
                key=RedisConstants.EMAIL_REGISTER_SMS_KEY+email;
            }else if (captchaType.equals(VerificationCodeReqDto.captchaType.RETRIEVE_PASSWORD.getType())){
                //判断手机号用户名是否重复，表里面存在相同手机号不进行添加
                if (flag) {
                    throw new BaseException("用户不存在");
                }
                key=RedisConstants.EMAIL_RETRIEVE_PASSWORD_SMS_KEY+email;
            }
        }
        redisCode = redisCache.getCacheObject(key);
        if (StringUtils.isEmpty(redisCode)) {
            throw new BaseException("验证码已过期");
        }
        String code = checkCodeReqDto.getCode();
        if (!code.equals(redisCode)) {
            throw new BaseException("验证码错误");
        }
    }
}
