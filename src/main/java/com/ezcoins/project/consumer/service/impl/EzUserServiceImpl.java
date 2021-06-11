package com.ezcoins.project.consumer.service.impl;

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
import com.ezcoins.project.common.service.PhoneService;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserLimitLog;
import com.ezcoins.project.consumer.entity.req.PhoneCaptchaReqDto;
import com.ezcoins.project.consumer.entity.req.EzUserReqDto;
import com.ezcoins.project.consumer.entity.req.UserLimitReqDto;
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
    public boolean checkUserUnique(String userName, String phone, String email, String inviteCode) {
        return ezUserMapper.checkUserUnique(userName, phone, email, inviteCode) == 0;
    }

    /**
     * 获取验证码
     *
     * @param inviteCode
     * @return
     */
    @Override
    public String getInviteCode(String inviteCode) {
        if (checkUserUnique(null, null, null, inviteCode)) {
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
    public EzUser selectUserBy(String userName, String phone, String email, String inviteCode) {
        return ezUserMapper.selectUserBy(userName, phone, email, inviteCode);
    }

    /**
     * 发送验证码
     *
     * @param captchaReqDto
     */
    @Override
    public void sendMsm(PhoneCaptchaReqDto captchaReqDto) {
        String phone = captchaReqDto.getPhonenumber();
        boolean flag = checkUserUnique(null, phone, null, null);
        String key = null;
        //注册验证吗
        if (PhoneCaptchaReqDto.PhoneCaptchaType.REGISTER.getType().equals(captchaReqDto.getCaptchaType())) {
            if (!flag) {
                throw new BaseException("手机号码已被注册");
            }
            key = RedisConstants.REGISTER_SMS_KEY;
        } else if (PhoneCaptchaReqDto.PhoneCaptchaType.RETRIEVE_PASSWORD.getType().equals(captchaReqDto.getCaptchaType())) {
            if (flag) {
                throw new BaseException("手机号码尚未注册");
            }
            key = RedisConstants.RETRIEVE_PASSWORD_SMS_KEY;
        }
        //1 从redis获取验证码，如果获取到直接返回
        String code = redisCache.getCacheObject(key + phone);
        if (StringUtils.isNotEmpty(code)) {
        } else {
//            code = VerifyCodeUtils.generateVerifyCode(6, VerifyCodeUtils.VERIFY_CODES_NUMBER);
//            try {
//                emailService.sendSimpleMail(new MailBean(phone, topic, content, code));
//                redisCache.setCacheObject(key + phone, code, 5, TimeUnit.MINUTES);
//                log.info("发送邮件成功，邮箱为：{},邮箱验证码为：{}", phone, code);
//            } catch (Exception e) {
//                log.info("发送邮件失败-异常", e);
//            }
            String code1 = phoneService.send(key, phone);
            if (code1==null) {
                throw new BaseException("短信发送失败");
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
        String userName = ezUserDto.getUserName();
        String password = ezUserDto.getPassword();
        String parentInviteCode = ezUserDto.getInviteCode();//父级邀请码
        String securityPassword = ezUserDto.getSecurityPassword();//安全密码
        EzUser EzUser = new EzUser();
        //获取redis验证码
        String redisCode ="666666";
//        String redisCode = redisCache.getCacheObject(RedisConstants.REGISTER_SMS_KEY + phone);
        if (StringUtils.isEmpty(redisCode)) {
            throw new BaseException("验证码已过期");
        }
        if (!code.equals(redisCode)) {
            throw new BaseException("验证码错误");
        }
        //判断手机号用户名是否重复，表里面存在相同手机号不进行添加
        if (!checkUserUnique(null, phone, null, null)) {
            throw new BaseException("手机号码已被注册");
        }
        if (!checkUserUnique(userName, null, null, null)) {
            throw new BaseException("用户名已存在");
        }
        String parentId = "0";
        if (StringUtils.isNotEmpty(parentInviteCode)) {
            EzUser ezUser1 = baseMapper.selectUserBy(null, null, null, parentInviteCode);
            if (ezUser1 == null) {
                throw new BaseException("邀请码错误");
            }
            parentId = ezUser1.getParentId();
        }
        //通过邀请码查询人
        EzUser.setParentId(parentId);
        //获得邀请码
        String inviteCode = getInviteCode(RandomUtil.generateByRandom(10));
        EzUser.setInviteCode(inviteCode);
        EzUser.setSecurityPassword(EncoderUtil.encode(securityPassword));
        EzUser.setUserName(userName);
        EzUser.setCreateBy(userName);
        EzUser.setPassword(EncoderUtil.encode(password));
        baseMapper.insert(EzUser);
    }

    /**
     * 用户登录
     *
     * @param authenticationRequest
     * @return
     */
    @Override
    public String login(JwtAuthenticationRequest authenticationRequest) {
        EzUser ezUser = baseMapper.selectUserBy(authenticationRequest.getUsername(), null, null, null);
        //用户不存在
        if (ezUser == null || UserStatus.DELETED.getCode().equals(ezUser.getIsDeleted())) {
            throw new UserException("登录用户不存在", null);
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
        //安全密码
        String securePassword = ezUserDto.getSecurityPassword();
        CheckException.checkNotEmpty(securePassword, () -> {
            ezUser.setSecurityPassword(securePassword);
        });
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
        switch (userLimitReqDto.getType()){
            case UserConstants.LOGIN_LIMIT :
                EzUser ezUser = new EzUser();
                ezUser.setUserId(userLimitReqDto.getUserId());
                ezUser.setStatus(userLimitReqDto.getOperate());
                baseMapper.updateById(ezUser);
                break;
            default :
                throw new BaseException("未知类型：{}",userLimitReqDto.getType());
        }

        EzUserLimitLog ezUserLimitLog = new EzUserLimitLog();
        BeanUtils.copyBeanProp(ezUserLimitLog,userLimitReqDto);
        ezUserLimitLog.setCreateBy(ContextHandler.getUserName());
        ezUserLimitLogService.save(ezUserLimitLog);
    }

}
