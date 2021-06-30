package com.ezcoins.project.consumer.controller.app;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.enums.user.KycStatus;
import com.ezcoins.constant.enums.user.UserKycStatus;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.config.entity.EzCountryConfig;
import com.ezcoins.project.config.service.EzCountryConfigService;
import com.ezcoins.project.consumer.entity.EzAdvertisingApprove;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.entity.req.AdvertisingReqDto;
import com.ezcoins.project.consumer.entity.req.VerificationCodeReqDto;
import com.ezcoins.project.consumer.entity.resp.VerifiedInfoRespDto;
import com.ezcoins.project.consumer.service.EzAdvertisingApproveService;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.project.acl.entity.req.JwtAuthenticationRequest;
import com.ezcoins.project.consumer.entity.req.EzUserReqDto;
import com.ezcoins.project.consumer.entity.req.UserKycReqDto;
import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
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

//    @ApiOperation(value = "用户修改密码/安全密码")
//    @PostMapping("updateUser")
//    public BaseResponse updateUser(@RequestBody UpdateUserBody updateUserBody) {
//        updateUserBody.setUserId(getUserId());
//        EzUserService.updateUser(updateUserBody);
//        return BaseResponse.success();
//}

    /**
     * 用户信息包括（用户名 用户id 用户邀请码）
     */
    @ApiOperation(value = "根据token获取用户信息")
    @GetMapping("getMemberInfo")
    public BaseResponse getEzUserInfo() {
        ezUserService.getById(getUserId());
        return BaseResponse.success();
    }
//
//    @ApiOperation(value = "找回密码")
//    @PostMapping("retrievePassword")
//    public BaseResponse retrievePassword(@RequestBody UpdateUserBody updateUserBody) {
//        CheckException.checkEmpty(updateUserBody.getCode(),"验证码不能为空");
//        //判断验证码是否正确
//        String code = redisCache.getCacheObject(Constants.RETRIEVE_PASSWORD_PHONE_CAPTCHA_KEY + updateUserBody.getPhone());
//        if (!code.equals(updateUserBody.getCode())){
//            return BaseResponse.error().message("验证码错误");
//        }
//        EzUserService.updateUser(updateUserBody);
//        return BaseResponse.success();
//    }

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
                verifiedInfoRespDto.setKycStatus("2");
            }
            if (advertisingApprove.getStatus().equals(KycStatus.PENDINGREVIEW.getCode()) || advertisingApprove.getStatus().equals(KycStatus.BY.getCode())) {
                if (advertisingApprove.getStatus().equals(KycStatus.BY.getCode())) {
                    verifiedInfoRespDto.setKycStatus("0");
                } else {
                    verifiedInfoRespDto.setKycStatus("1");
                }
                verifiedInfoRespDto.setMargin(advertisingApprove.getMargin());
            }
        } else {
            verifiedInfoRespDto.setKycStatus("3");
        }
        return Response.success(verifiedInfoRespDto);
    }

    @ApiOperation(value = "国家列表")
    @GetMapping("countryList")
    public ResponseList<EzCountryConfig> countryList() {
        return ResponseList.success(countryConfigService.list());
    }
}
