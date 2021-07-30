package com.ezcoins.project.consumer.controller.app;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.constant.enums.user.KycStatus;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.config.entity.EzCountryConfig;
import com.ezcoins.project.config.service.EzCountryConfigService;
import com.ezcoins.project.consumer.entity.EzAdvertisingApprove;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.entity.req.*;
import com.ezcoins.project.consumer.entity.resp.SidebarInfoRespDto;
import com.ezcoins.project.consumer.entity.resp.VerifiedInfoRespDto;
import com.ezcoins.project.consumer.service.EzAdvertisingApproveService;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.project.acl.entity.req.JwtAuthenticationRequest;
import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.utils.EncoderUtil;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author:
 * @Email:
 * @Description:
 * @Date:2021/5/19 16:30
 * @Version:1.0
 */
@Api(tags = "APP-用户模块")
@RestController
@Accessors(chain = true)
@RequestMapping("/consumer/app")
public class ConsumerController extends BaseController {

    @Autowired
    private EzUserService ezUserService;

    @Autowired
    private EzUserKycService kycService;

    @Autowired
    private EzCountryConfigService countryConfigService;

    @Autowired
    private EzAdvertisingApproveService approveService;

    @Autowired
    private EzAdvertisingBusinessService businessService;

    @Autowired
    private RedisCache redisCache;


    @ApiOperation(value = "发送短信/邮箱验证码")
    @PostMapping("sendMsm")
    @NoRepeatSubmit
    public Response sendMsm(@RequestBody @Validated VerificationCodeReqDto codeReqDto) {
        if ("1".equals(codeReqDto.getType())) {
            return Response.error("手机验证尚未开启，请先使用邮箱验证");
        }
        ezUserService.sendMsm(codeReqDto);
        return Response.success();
    }

    @ApiOperation(value = "用户注册")
    @PostMapping("register")
    @NoRepeatSubmit
    public Response registerUser(@RequestBody @Validated EzUserReqDto ezUserDto) {
        ezUserService.addUser(ezUserDto,false);
        return Response.success();
    }

    @ApiOperation(value = "用户登录")
    @PostMapping("login")
    @NoRepeatSubmit
    public Response<Map<String,String>> login(@RequestBody @Validated JwtAuthenticationRequest jwtAuthenticationRequest) {
        return Response.success(ezUserService.login(jwtAuthenticationRequest)).message("登录成功");
    }

    @ApiOperation(value = "国家列表")
    @GetMapping("countryList")
    public ResponseList<EzCountryConfig> countryList() {
        return ResponseList.success(countryConfigService.list());
    }

    /**
     * 用户信息包括（用户名 用户id 用户邀请码）
     */
    @ApiOperation(value = "根据token获取用户信息")
    @GetMapping("getMemberInfo")
    public Response getEzUserInfo() {
        ezUserService.getById(getUserId());
        return Response.success();
    }

    @NoRepeatSubmit
    @ApiOperation(value = "找回密码")
    @PostMapping("retrievePassword")
    public Response retrievePassword(@RequestBody @Validated RetrievePwReqDto retrievePwReqDto) {
        String phoneOrEmail = retrievePwReqDto.getPhoneOrEmail();
        LambdaQueryWrapper<EzUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzUser::getPhone, phoneOrEmail);
        EzUser ezUser = ezUserService.getOne(lambdaQueryWrapper);
        String codeRedis = redisCache.getCacheObject(RedisConstants.PHONE_RETRIEVE_PASSWORD_SMS_KEY + phoneOrEmail);//判断验证码是否正确
        if (ezUser == null) {
            LambdaQueryWrapper<EzUser> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper2.eq(EzUser::getEmail, phoneOrEmail);
            ezUser = ezUserService.getOne(lambdaQueryWrapper2);
            codeRedis = redisCache.getCacheObject(RedisConstants.EMAIL_RETRIEVE_PASSWORD_SMS_KEY + phoneOrEmail);
        }
        if (ezUser == null) {
            return Response.error(MessageUtils.message("用户不存在"));
        }
        if (!codeRedis.equals(retrievePwReqDto.getCode())) {
            return Response.error(MessageUtils.message("验证码错误"));
        }
        ezUser.setPassword(EncoderUtil.encode(retrievePwReqDto.getNewPassword()));
        ezUserService.updateById(ezUser);

        redisCache.deleteObject(RedisConstants.EMAIL_RETRIEVE_PASSWORD_SMS_KEY + phoneOrEmail);
        return Response.success();
    }

    @NoRepeatSubmit
    @ApiOperation(value = "用户实名认证")
    @PostMapping("verified")
    @AuthToken
    public Response verified(@RequestBody @Validated UserKycReqDto userKycReqDto) {
        userKycReqDto.setUserId(ContextHandler.getUserId());
        kycService.verified(userKycReqDto);
        return Response.success();
    }

    @NoRepeatSubmit
    @ApiOperation(value = "用户高级认证")
    @PostMapping("advertisingVerified")
    @AuthToken
    public Response advertisingVerified(@RequestBody @Validated AdvertisingReqDto advertisingReqDto) {
        advertisingReqDto.setUserId(ContextHandler.getUserId());
        approveService.verified(advertisingReqDto);
        return Response.success();
    }


    @ApiOperation(value = "认证详情")
    @PostMapping("verifiedInfo")
    @AuthToken
    public Response<VerifiedInfoRespDto> verifiedInfo() {
        //根据用户认证状态
        String userId = ContextHandler.getUserId();
        EzUser user = ezUserService.getById(userId);
        VerifiedInfoRespDto verifiedInfoRespDto = new VerifiedInfoRespDto();
        //查看用户是否有过认证
        LambdaQueryWrapper<EzUserKyc> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzUserKyc::getUserId, userId);
        EzUserKyc ezUserKyc = kycService.getOne(lambdaQueryWrapper);

        //已认证  查看用户认证
        if (StringUtils.isNotNull(ezUserKyc)) {
            if (ezUserKyc.getStatus().equals(KycStatus.REFUSE.getCode())) {
                verifiedInfoRespDto.setKycStatus("2");
            }
            if (ezUserKyc.getStatus().equals(KycStatus.PENDINGREVIEW.getCode()) || ezUserKyc.getStatus().equals(KycStatus.BY.getCode())) {
                if (ezUserKyc.getStatus().equals(KycStatus.BY.getCode())) {
                    verifiedInfoRespDto.setKycStatus("0");
                } else {
                    verifiedInfoRespDto.setKycStatus("1");
                }
                verifiedInfoRespDto.setAdvertisingStatus(user.getLevel());
                verifiedInfoRespDto.setLastName(ezUserKyc.getLastName());
                verifiedInfoRespDto.setFirstName(ezUserKyc.getFirstName());
                verifiedInfoRespDto.setIdentityCard(ezUserKyc.getIdentityCard());
                verifiedInfoRespDto.setUserId(userId);
                LambdaQueryWrapper<EzCountryConfig> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(EzCountryConfig::getCountryCode, user.getCountryCode());
                EzCountryConfig one = countryConfigService.getOne(queryWrapper);
                String locale = LocaleContextHolder.getLocale().toString();
                if ("zh_CN".equals(locale)) {
                    verifiedInfoRespDto.setCountry(one.getCountryName());
                } else if ("en_US".equals(locale)) {
                    verifiedInfoRespDto.setCountry(one.getCountryNameEn());
                }
            }
        } else {
            verifiedInfoRespDto.setKycStatus("3");
        }
        LambdaQueryWrapper<EzAdvertisingApprove> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAdvertisingApprove::getUserId, userId);
        EzAdvertisingApprove advertisingApprove = approveService.getOne(queryWrapper);
        if (StringUtils.isNotNull(advertisingApprove)) {
            if (advertisingApprove.getStatus().equals(KycStatus.REFUSE.getCode())) {
                verifiedInfoRespDto.setAdvertisingStatus("2");
            }
            if (advertisingApprove.getStatus().equals(KycStatus.PENDINGREVIEW.getCode()) || advertisingApprove.getStatus().equals(KycStatus.BY.getCode())) {
                if (advertisingApprove.getStatus().equals(KycStatus.BY.getCode())) {
                    verifiedInfoRespDto.setAdvertisingStatus("0");
                } else {
                    verifiedInfoRespDto.setAdvertisingStatus("1");
                }
                verifiedInfoRespDto.setMargin(advertisingApprove.getMargin());
            }
        } else {
            verifiedInfoRespDto.setAdvertisingStatus("3");
        }
        return Response.success(verifiedInfoRespDto);
    }


    @ApiOperation(value = "侧边栏信息")
    @GetMapping("sidebarInfo")
    @AuthToken
    public Response<SidebarInfoRespDto> sidebarInfo() {
        SidebarInfoRespDto infoRespDto = new SidebarInfoRespDto();

        String userId = ContextHandler.getUserId();
        EzUser ezUser = ezUserService.getById(userId);

        LambdaQueryWrapper<EzUserKyc> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzUserKyc::getUserId, userId);
        EzUserKyc one = kycService.getOne(queryWrapper);

        //0:待审核 1：通过  2：未通过 3：未认证
        if (one != null && one.getStatus().equals(KycStatus.BY.getCode())) {
            infoRespDto.setFirstName(one.getFirstName());
            infoRespDto.setLastName(one.getLastName());
        }
        if (one!=null){
            infoRespDto.setKycStatus(one.getStatus());
        }else {
            infoRespDto.setKycStatus("3");
        }
        LambdaQueryWrapper<EzAdvertisingBusiness> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, userId);
        EzAdvertisingBusiness businessServiceOne = businessService.getOne(businessLambdaQueryWrapper);

        String isSetting = StringUtils.isEmpty(businessServiceOne.getSecurityPassword()) ? "1" : "0";
        infoRespDto.setEmail(ezUser.getEmail());
        infoRespDto.setPhone(ezUser.getPhone());
        infoRespDto.setUserId(ezUser.getUserId());
        infoRespDto.setPhoneArea(ezUser.getPhoneArea());
        infoRespDto.setIsSetting(isSetting);

        LambdaQueryWrapper<EzCountryConfig> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(EzCountryConfig::getCountryCode, ezUser.getCountryCode());
        EzCountryConfig one1 = countryConfigService.getOne(queryWrapper1);
        String locale = LocaleContextHolder.getLocale().toString();
        if ("zh_CN".equals(locale)) {
            infoRespDto.setCountryName(one1.getCountryName());
        } else if ("en_US".equals(locale)) {
            infoRespDto.setCountryName(one1.getCountryNameEn());
        }
        infoRespDto.setCountryCode(ezUser.getCountryCode());
        infoRespDto.setCountryTelCode(one1.getCountryTelCode());
        infoRespDto.setNationalFlagAddr(one1.getNationalFlagAddr());
        infoRespDto.setShareAddr("https://www.baidu.com");
        return Response.success(infoRespDto);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "修改密码")
    @PostMapping("updatePassword")
    @Log(title = "修改安全密码", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    @AuthToken
    public Response updatePassword(@RequestBody @Validated PasswordUpdateReqDto passwordUpdateReqDto) {
        LambdaQueryWrapper<EzUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzUser::getUserId, ContextHandler.getUserId());
        EzUser user = ezUserService.getOne(queryWrapper);
        boolean matches = EncoderUtil.matches(passwordUpdateReqDto.getPassword(), user.getPassword());
        if (matches) {
            if (EncoderUtil.matches(passwordUpdateReqDto.getNewPassword(), user.getPassword())) {
                return Response.error(MessageUtils.message("旧密码与新密码相同"));
            }
            user.setPassword(EncoderUtil.encode(passwordUpdateReqDto.getNewPassword()));
            ezUserService.updateById(user);
        } else {
            return Response.error(MessageUtils.message("旧密码输入错误"));
        }
        return Response.success();
    }

    @NoRepeatSubmit
    @ApiOperation(value = "修改安全密码")
    @PostMapping("updateSecurityPassword")
    @AuthToken
    @Log(title = "修改安全密码", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response updateSecurityPassword(@RequestBody @Validated PasswordUpdateReqDto passwordUpdateReqDto) {
        LambdaQueryWrapper<EzAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAdvertisingBusiness::getUserId, ContextHandler.getUserId());
        EzAdvertisingBusiness one = businessService.getOne(queryWrapper);
        if (StringUtils.isEmpty(one.getSecurityPassword())){
            return Response.error(MessageUtils.message("请先设置安全密码"));
        }
        boolean matches = EncoderUtil.matches(passwordUpdateReqDto.getPassword(), one.getSecurityPassword());
        if (matches) {
            if (EncoderUtil.matches(passwordUpdateReqDto.getNewPassword(), one.getSecurityPassword())) {
                return Response.error(MessageUtils.message("旧密码与新密码相同"));
            }
            one.setSecurityPassword(EncoderUtil.encode(passwordUpdateReqDto.getNewPassword()));
            businessService.updateById(one);
        } else {
            return Response.error(MessageUtils.message("旧密码输入错误"));
        }
        return Response.success();
    }

    @NoRepeatSubmit
    @ApiOperation(value = "绑定安全信息")
    @PostMapping("bindSecurityInfo")
    @AuthToken
    @Log(title = "绑定安全信息", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response bindSecurityInfo(@RequestBody BingSecurityReqDto bingSecurityReqDto) {
        String type = bingSecurityReqDto.getType();
        String phoneOrEmail = bingSecurityReqDto.getPhoneOrEmail();
        String code = bingSecurityReqDto.getCode();
        String phoneArea = bingSecurityReqDto.getPhoneArea();

        String key = RedisConstants.PHONE_BIND_INFO_SMS_KEY;
        String key2 = RedisConstants.EMAIL_BIND_INFO_SMS_KEY;

        String rmKey = null;
        String redisCode;
        LambdaQueryWrapper<EzUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzUser::getUserId, ContextHandler.getUserId());
        EzUser ezUser = ezUserService.getOne(lambdaQueryWrapper);

        LambdaQueryWrapper<EzUser> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        if ("1".equals(type)) {
            if (StringUtils.isNotEmpty(ezUser.getPhone())) {
                return Response.error(MessageUtils.message("用户已绑定手机号"));
            }
            lambdaQueryWrapper2.eq(EzUser::getPhone, phoneOrEmail);
            lambdaQueryWrapper2.eq(EzUser::getPhoneArea, phoneArea);
            EzUser one = ezUserService.getOne(lambdaQueryWrapper2);
            if (null != one) {
                return Response.error(MessageUtils.message("手机号已被绑定"));
            }
            redisCode = redisCache.getCacheObject(key + phoneArea + phoneOrEmail);
            ezUser.setPhone(phoneOrEmail);
            ezUser.setPhoneArea(phoneArea);
            rmKey = key + phoneArea + phoneOrEmail;
        } else {
            if (StringUtils.isNotEmpty(ezUser.getEmail())) {
                return Response.error(MessageUtils.message("用户已绑定邮箱"));
            }
            lambdaQueryWrapper2.eq(EzUser::getEmail, phoneOrEmail);
            EzUser one = ezUserService.getOne(lambdaQueryWrapper2);
            if (null != one) {
                return Response.error(MessageUtils.message("邮箱已被绑定"));
            }
            redisCode = redisCache.getCacheObject(key2 + phoneOrEmail);
            ezUser.setEmail(phoneOrEmail);
            rmKey = key2 + phoneOrEmail;
        }
        if (StringUtils.isEmpty(redisCode)) {
            return Response.error(MessageUtils.message("验证码已过期"));
        }

        if (!code.equals(redisCode)) {
            return Response.error(MessageUtils.message("验证码错误"));
        }
        ezUserService.updateById(ezUser);
        redisCache.deleteObject(rmKey);
        return Response.success();
    }

}
