package com.ezcoins.project.consumer.controller.app;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.Constants;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.constant.enums.user.KycStatus;
import com.ezcoins.constant.enums.user.UserKycStatus;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.project.config.entity.EzCountryConfig;
import com.ezcoins.project.config.service.EzCountryConfigService;
import com.ezcoins.project.consumer.entity.EzAdvertisingApprove;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.entity.body.UpdateUserBody;
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
import com.ezcoins.response.BaseResponse;
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
    public BaseResponse sendMsm(@RequestBody @Validated VerificationCodeReqDto codeReqDto) {
        ezUserService.sendMsm(codeReqDto);
        return BaseResponse.success();
    }

    @ApiOperation(value = "用户注册")
    @PostMapping("register")
    @NoRepeatSubmit
    public BaseResponse registerUser(@RequestBody @Validated EzUserReqDto ezUserDto) {
        ezUserService.addUser(ezUserDto);
        return BaseResponse.success();
    }

    @ApiOperation(value = "用户登录")
    @PostMapping("login")
    @NoRepeatSubmit
    public BaseResponse login(@RequestBody @Validated JwtAuthenticationRequest jwtAuthenticationRequest) {
        return BaseResponse.success().message("登录成功").data("token", ezUserService.login(jwtAuthenticationRequest));
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
    public BaseResponse getEzUserInfo() {
        ezUserService.getById(getUserId());
        return BaseResponse.success();
    }

    @NoRepeatSubmit
    @ApiOperation(value = "找回密码")
    @PostMapping("retrievePassword")
    public BaseResponse retrievePassword(@RequestBody @Validated RetrievePwReqDto retrievePwReqDto) {
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
            return BaseResponse.error(MessageUtils.message("用户不存在"));
        }
        if (!codeRedis.equals(retrievePwReqDto.getCode())) {
            return BaseResponse.error(MessageUtils.message("验证码错误"));
        }
        ezUser.setPassword(EncoderUtil.encode(retrievePwReqDto.getNewPassword()));
        ezUserService.updateById(ezUser);
        return BaseResponse.success();
    }

    @NoRepeatSubmit
    @ApiOperation(value = "用户实名认证")
    @PostMapping("verified")
    @AuthToken
    public BaseResponse verified(@RequestBody @Validated UserKycReqDto userKycReqDto) {
        userKycReqDto.setUserId(ContextHandler.getUserId());
        kycService.verified(userKycReqDto);
        return BaseResponse.success();
    }

    @NoRepeatSubmit
    @ApiOperation(value = "用户高级认证")
    @PostMapping("advertisingVerified")
    @AuthToken
    public BaseResponse advertisingVerified(@RequestBody @Validated AdvertisingReqDto advertisingReqDto) {
        advertisingReqDto.setUserId(ContextHandler.getUserId());
        approveService.verified(advertisingReqDto);
        return BaseResponse.success();
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
                verifiedInfoRespDto.setRealName(ezUserKyc.getRealName());
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
        String userId = ContextHandler.getUserId();
        EzUser ezUser = ezUserService.getById(userId);
        LambdaQueryWrapper<EzUserKyc> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzUserKyc::getUserId, userId);
        EzUserKyc one = kycService.getOne(queryWrapper);
        SidebarInfoRespDto infoRespDto = new SidebarInfoRespDto();
        infoRespDto.setEmail(ezUser.getEmail());
        infoRespDto.setPhone(ezUser.getPhone());
        infoRespDto.setUserId(ezUser.getUserId());
        infoRespDto.setRealName(one.getRealName());
        infoRespDto.setPhoneArea(ezUser.getPhoneArea());
        return Response.success(infoRespDto);
    }


    @NoRepeatSubmit
    @ApiOperation(value = "修改安全密码")
    @PostMapping("updateSecurityPassword")
    @Log(title = "修改安全密码", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse updateSecurityPassword(@RequestBody PasswordUpdateReqDto passwordUpdateReqDto) {
        LambdaQueryWrapper<EzAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAdvertisingBusiness::getUserId, ContextHandler.getUserId());
        EzAdvertisingBusiness one = businessService.getOne(queryWrapper);

        boolean matches = EncoderUtil.matches(passwordUpdateReqDto.getPassword(), one.getSecurityPassword());
        if (matches) {
            if (EncoderUtil.matches(passwordUpdateReqDto.getNewPassword(), one.getSecurityPassword())) {
                return BaseResponse.error(MessageUtils.message("旧密码与新密码相同"));
            }
            one.setSecurityPassword(EncoderUtil.encode(passwordUpdateReqDto.getPassword()));
            businessService.updateById(one);
        } else {
            return BaseResponse.error(MessageUtils.message("旧密码输入错误"));
        }
        return BaseResponse.success();
    }

    @NoRepeatSubmit
    @ApiOperation(value = "绑定安全信息")
    @PostMapping("bindSecurityInfo")
    @AuthToken
    @Log(title = "绑定安全信息", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse bindSecurityInfo(@RequestBody BingSecurityReqDto bingSecurityReqDto) {
        String type = bingSecurityReqDto.getType();
        String phoneOrEmail = bingSecurityReqDto.getPhoneOrEmail();
        String code = bingSecurityReqDto.getCode();
        String phoneArea = bingSecurityReqDto.getPhoneArea();

        String key = RedisConstants.PHONE_BIND_INFO_SMS_KEY;
        String key2 = RedisConstants.EMAIL_BIND_INFO_SMS_KEY;
        String redisCode;
        LambdaQueryWrapper<EzUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzUser::getUserId, ContextHandler.getUserId());
        EzUser ezUser = ezUserService.getOne(lambdaQueryWrapper);

        LambdaQueryWrapper<EzUser> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        if ("1".equals(type)) {
            if (StringUtils.isNotEmpty(ezUser.getPhone())) {
                return BaseResponse.error("用户已绑定手机号");
            }
            lambdaQueryWrapper2.eq(EzUser::getPhone, phoneOrEmail);
            lambdaQueryWrapper2.eq(EzUser::getPhoneArea, phoneArea);
            EzUser one = ezUserService.getOne(lambdaQueryWrapper2);
            if (null != one) {
                return BaseResponse.error("手机号已被绑定");
            }
            redisCode = redisCache.getCacheObject(key + phoneArea + phoneOrEmail);
            ezUser.setPhone(phoneOrEmail);
            ezUser.setPhoneArea(phoneArea);
        } else {
            if (StringUtils.isNotEmpty(ezUser.getEmail())) {
                return BaseResponse.error("用户已绑定邮箱");
            }
            lambdaQueryWrapper2.eq(EzUser::getEmail, phoneOrEmail);
            EzUser one = ezUserService.getOne(lambdaQueryWrapper2);
            if (null != one) {
                return BaseResponse.error("邮箱已被绑定");
            }
            redisCode = redisCache.getCacheObject(key2 + phoneOrEmail);
            ezUser.setEmail(phoneOrEmail);
        }
        if (StringUtils.isEmpty(redisCode)) {
            return BaseResponse.error(MessageUtils.message("验证码已过期"));
        }

        if (!code.equals(redisCode)) {
            return BaseResponse.error(MessageUtils.message("验证码错误"));
        }

        ezUserService.updateById(ezUser);
        return BaseResponse.success();
    }


}
