package com.ezcoins.project.otc.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.otc.entity.EzOtcConfig;
import com.ezcoins.project.otc.entity.EzSellConfig;
import com.ezcoins.project.otc.entity.req.SellConfigReqDto;
import com.ezcoins.project.otc.service.EzSellConfigService;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.utils.BeanUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 一键卖币配置 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-28
 */
@RestController
@RequestMapping("/admin/otc/ezSellConfig")
public class EzSellConfigController {


    @Autowired
    private EzSellConfigService sellConfigService;

    @ApiOperation(value = "一键卖币配置")
    @PostMapping("sellConfig")
    @AuthToken
    public ResponseList<EzSellConfig> sellConfig() {
        return ResponseList.success(sellConfigService.list());
    }


    @NoRepeatSubmit
    @ApiOperation(value = "修改一键卖币配置")
    @PostMapping("updateSellConfig")
    @AuthToken
    @Log(title = "修改一键卖币配置", businessType = BusinessType.UPDATE, operatorType = OperatorType.MANAGE)
    public BaseResponse updateSellConfig(@RequestBody SellConfigReqDto sellConfigReqDto) {
        EzSellConfig ezSellConfig = new EzSellConfig();
        BeanUtils.copyBeanProp(ezSellConfig,sellConfigReqDto);
        ezSellConfig.setUpdateBy(ContextHandler.getUserName());
        sellConfigService.updateById(ezSellConfig);
        return BaseResponse.success();
    }








}

