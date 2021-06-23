package com.ezcoins.project.consumer.controller.app;


import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.base.BaseController;
import com.ezcoins.project.config.entity.EzCountryConfig;
import com.ezcoins.project.config.service.EzCountryConfigService;
import com.ezcoins.project.consumer.entity.req.VerificationCodeReqDto;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.project.acl.entity.req.JwtAuthenticationRequest;
import com.ezcoins.project.consumer.entity.req.EzUserReqDto;
import com.ezcoins.project.consumer.entity.req.UserKycReqDto;
import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.ResponseList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
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
        return BaseResponse.success().message("登录成功").data("token",ezUserService.login(jwtAuthenticationRequest));
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
    public BaseResponse verified(@RequestBody @Validated UserKycReqDto userKycReqDto) {
        userKycReqDto.setUserId(getUserId());
        kycService.verified(userKycReqDto);
        return BaseResponse.success();
    }

    @ApiOperation(value = "国家列表")
    @GetMapping("countryList")
    public ResponseList<EzCountryConfig> countryList() {
        return ResponseList.success(countryConfigService.list());
    }

}
