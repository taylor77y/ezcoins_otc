package com.ezcoins.project.config.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.project.common.service.PhoneService;
import com.ezcoins.project.config.entity.EzSmsConfig;
import com.ezcoins.project.config.entity.req.SmsReqDto;
import com.ezcoins.project.config.service.EzSmsConfigService;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import com.ezcoins.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-08
 */

@Api(tags = "Admin-Sms管理模块")
@RestController
@RequestMapping("/admin/config/smsConfig")
public class EzSmsConfigController {


    @Autowired
    private EzSmsConfigService ezSmsConfigService;


    @Autowired
    private RedisCache redisCache;


    @Autowired
    private PhoneService phoneService;

    @ApiOperation(value = "短信配置")
    @GetMapping("/smsConfigs")
    @AuthToken
    public Response<EzSmsConfig> smsConfigs(){
        EzSmsConfig ezcoinsSmsConfig = ezSmsConfigService.getById(1);
        ezcoinsSmsConfig.setBalance(phoneService.getBalance());
        return Response.success(ezcoinsSmsConfig);
    }

    @ApiOperation(value = "修改短信配置")
    @PutMapping("/updateConfig")
    @AuthToken
    @Log(title = "修改短信配置", businessType = BusinessType.UPDATE, operatorType = OperatorType.MANAGE)
    public BaseResponse updateConfig(@RequestBody SmsReqDto smsReqDto){
        EzSmsConfig ezcoinsSmsConfig = new EzSmsConfig();
        BeanUtils.copyBeanProp(ezcoinsSmsConfig,smsReqDto);
        ezSmsConfigService.updateById(ezcoinsSmsConfig);
        redisCache.setCacheObject(RedisConstants.SMS_CONFIG_KEY,ezcoinsSmsConfig,5, TimeUnit.MINUTES);
        return BaseResponse.success();
    }



}

