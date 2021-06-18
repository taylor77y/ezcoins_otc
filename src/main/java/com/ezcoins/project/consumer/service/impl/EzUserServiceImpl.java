package com.ezcoins.project.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.UserConstants;
import com.ezcoins.constant.enums.LoginType;
import com.ezcoins.constant.enums.user.UserStatus;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.exception.user.UserException;
import com.ezcoins.exception.user.UserPasswordNotMatchException;
import com.ezcoins.project.acl.entity.req.JwtAuthenticationRequest;
import com.ezcoins.project.common.mq.producer.LoginProducer;
import com.ezcoins.project.common.service.EmailService;
import com.ezcoins.project.common.service.MailBean;
import com.ezcoins.project.common.service.PhoneService;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserLimitLog;
import com.ezcoins.project.consumer.entity.req.EzUserReqDto;
import com.ezcoins.project.consumer.entity.req.UserLimitReqDto;
import com.ezcoins.project.consumer.entity.req.VerificationCodeReqDto;
import com.ezcoins.project.consumer.mapper.EzUserMapper;
import com.ezcoins.project.consumer.service.EzUserLimitLogService;
import com.ezcoins.project.consumer.service.EzUserService;
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
    private EzUserLimitLogService ezUserLimitLogService;


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
        String code =null;
        //手机验证码
        if (type.equals(VerificationCodeReqDto.Type.PHONE.getType())) {
            if (StringUtils.isEmpty(phoneArea)) {
                throw new BaseException(MessageUtils.message("请选择国际区号"));
            }
            boolean flag = checkUserUnique(null, verificationNumber, phoneArea, null, null);
            //注册验证吗
            if (VerificationCodeReqDto.captchaType.REGISTER.getType().equals(captchaType)) {
                if (!flag) {
                    throw new BaseException(MessageUtils.message("手机号已被注册"));
                }
                key = RedisConstants.PHONE_REGISTER_SMS_KEY;
            } else if (VerificationCodeReqDto.captchaType.RETRIEVE_PASSWORD.getType().equals(captchaType)) {
                if (flag) {
                    throw new BaseException(MessageUtils.message("手机号尚未绑定"));
                }
                key = RedisConstants.PHONE_RETRIEVE_PASSWORD_SMS_KEY;
            }
            code=redisCache.getCacheObject(key + phoneArea+verificationNumber);

        } else if (type.equals(VerificationCodeReqDto.Type.EMAIL.getType())) {
            boolean flag = checkUserUnique(null, null, null, verificationNumber, null);
            //注册验证吗
            if (VerificationCodeReqDto.captchaType.REGISTER.getType().equals(captchaType)) {
                if (!flag) {
                    throw new BaseException(MessageUtils.message("邮箱已被注册"));
                }
                key = RedisConstants.EMAIL_REGISTER_SMS_KEY;
            } else if (VerificationCodeReqDto.captchaType.RETRIEVE_PASSWORD.getType().equals(captchaType)) {
                if (flag) {
                    throw new BaseException(MessageUtils.message("邮箱尚未绑定"));
                }
                key = RedisConstants.EMAIL_RETRIEVE_PASSWORD_SMS_KEY;
            }
            code=redisCache.getCacheObject(key + verificationNumber);
        }
        //1 从redis获取验证码，如果获取到直接返回

        if (StringUtils.isNotEmpty(code)) {
        } else {
            //随机产生6位数字
            code= RandomUtil.getSixBitRandom();
            if (type.equals(VerificationCodeReqDto.Type.PHONE.getType())) {
                phoneService.send(key, code,phoneArea+verificationNumber);
            }else if (type.equals(VerificationCodeReqDto.Type.EMAIL.getType())){
                try {
                    emailService.sendComplexMail(key,verificationNumber, MessageUtils.message("感谢您使用亿智交易平台"), code);
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
    public void addUser(EzUserReqDto ezUserDto) {
        //获取注册的数据
        String code = ezUserDto.getCode();
        String phone = ezUserDto.getPhone();
        String phoneArea = ezUserDto.getPhoneArea();
        String email = ezUserDto.getEmail();
        String type = ezUserDto.getType();
        String password = ezUserDto.getPassword();
        String parentInviteCode = ezUserDto.getInviteCode();//父级邀请码
        String countryCode = ezUserDto.getCountryCode();//国级代码

        EzUser ezUser = new EzUser();
        //获取redis验证码
        String redisCode = "666666";
        if (type.equals(VerificationCodeReqDto.Type.PHONE.getType())){
            if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(phoneArea)){
                throw new BaseException(MessageUtils.message("手机号不能为空"));
            }
            //判断手机号用户名是否重复，表里面存在相同手机号不进行添加
            if (!checkUserUnique(null, phone,phoneArea,null, null)) {
                throw new BaseException(MessageUtils.message("手机号码已被注册"));
            }
            ezUser.setPhone(phone);
            ezUser.setPhoneArea(phoneArea);
            ezUser.setUserName(phone);
            ezUser.setCreateBy(phone);
//            redisCode=redisCache.getCacheObject(RedisConstants.PHONE_REGISTER_SMS_KEY + phone);
        }else if (type.equals(VerificationCodeReqDto.Type.EMAIL.getType())){
            //判断手机号用户名是否重复，表里面存在相同手机号不进行添加
            if (!checkUserUnique(null, null,null,email, null)) {
                throw new BaseException(MessageUtils.message("邮箱已被注册"));
            }
            ezUser.setUserName(email);
            ezUser.setEmail(email);
            ezUser.setCreateBy(email);
//            redisCode=redisCache.getCacheObject(RedisConstants.EMAIL_REGISTER_SMS_KEY + email);
        }
        if (StringUtils.isEmpty(redisCode)) {
            throw new BaseException(MessageUtils.message("验证码已过期"));
        }
        if (!code.equals(redisCode)) {
            throw new BaseException(MessageUtils.message("验证码错误"));
        }
        String parentId = "0";
        if (StringUtils.isNotEmpty(parentInviteCode)) {
            EzUser ezUser1 = baseMapper.selectUserBy(null, null, null,null, parentInviteCode);
            if (ezUser1 == null) {
                throw new BaseException(MessageUtils.message("邀请码错误"));
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
    }

    /**
     * 用户登录
     *
     * @param authenticationRequest
     * @return
     */
    @Override
    public String login(JwtAuthenticationRequest authenticationRequest) {
        //登录的时候 如果绑定了邮箱/电话号码 都可以用来登录
        LambdaQueryWrapper<EzUser> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzUser::getPhone,authenticationRequest.getUsername()).or().eq(EzUser::getEmail,authenticationRequest.getUsername());
        EzUser ezUser = baseMapper.selectOne(lambdaQueryWrapper);
        //用户不存在
        if (ezUser == null || UserStatus.DELETED.getCode().equals(ezUser.getIsDeleted())) {
            throw new UserException("登录用户已被删除", null);
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
        loginProducer.sendMsgLoginFollowUp(ezUser.getUserName(), userId, ezUser.getNickName(), LoginType.APP.getType());
        return token;
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
        //手机
        String phone = ezUserDto.getPhone();
        CheckException.checkNotEmpty(phone, () -> {
            ezUser.setPhone(phone);
        });
//        //安全密码
//        String securePassword = ezUserDto.getSecurityPassword();
//        CheckException.checkNotEmpty(securePassword, () -> {
//            ezUser.setSecurityPassword(securePassword);
//        });
        ezUser.setUpdateBy(SecurityUtils.getUsername());
        ezUser.setUpdateTime(DateUtils.getNowDate());
        CheckException.checkDb(baseMapper.updateById(ezUser), "用户更新失败");
    }

    /**
     * 封禁 解封 账号
     *
     * @param userLimitReqDto
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void titleOrUnnumber(UserLimitReqDto userLimitReqDto) {
        switch (userLimitReqDto.getType()) {
            case UserConstants.LOGIN_LIMIT:
                EzUser ezUser = new EzUser();
                ezUser.setUserId(userLimitReqDto.getUserId());
                ezUser.setStatus(userLimitReqDto.getOperate());
                baseMapper.updateById(ezUser);
                break;
            default:
                throw new BaseException("未知类型：{}", userLimitReqDto.getType());
        }

        EzUserLimitLog ezUserLimitLog = new EzUserLimitLog();
        BeanUtils.copyBeanProp(ezUserLimitLog, userLimitReqDto);
        ezUserLimitLog.setCreateBy(ContextHandler.getUserName());
        ezUserLimitLogService.save(ezUserLimitLog);
    }
}
