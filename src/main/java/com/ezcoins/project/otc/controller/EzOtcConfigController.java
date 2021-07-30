package com.ezcoins.project.otc.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzOtcConfig;
import com.ezcoins.project.otc.entity.EzPaymentMethod;
import com.ezcoins.project.otc.entity.req.PaymentMethodReqDto;
import com.ezcoins.project.otc.service.EzOtcConfigService;
import com.ezcoins.project.otc.service.EzOtcOrderService;
import com.ezcoins.response.Response;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * otc配置 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-21
 */
@RestController
@Api(tags = "Admin-OTC基本配置")
@RequestMapping("/otc/ezOtcConfig")
public class EzOtcConfigController {

    @Autowired
    private EzOtcConfigService otcConfigService;

    @ApiOperation(value = "OTC基本配置")
    @PostMapping("otcConfig")
    @AuthToken
    public Response<EzOtcConfig> otcConfig() {
        return Response.success(otcConfigService.getById("1"));
    }


    @NoRepeatSubmit
    @ApiOperation(value = "修改OTC基本配置")
    @PostMapping("updateOtcConfig")
    @AuthToken
    @Log(title = "OTC模块", logInfo ="修改OTC基本配置", operatorType = OperatorType.MANAGE)
    public Response updateOtcConfig(@RequestBody EzOtcConfig ezOtcConfig) {
        otcConfigService.updateById(ezOtcConfig);
        return Response.success();
    }

}

