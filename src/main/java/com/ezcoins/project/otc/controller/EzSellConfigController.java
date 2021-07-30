package com.ezcoins.project.otc.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.otc.entity.EzOneSellConfig;
import com.ezcoins.project.otc.entity.EzOtcConfig;
import com.ezcoins.project.otc.entity.EzOneSellConfig;
import com.ezcoins.project.otc.entity.req.SellConfigReqDto;
import com.ezcoins.project.otc.entity.req.StatusReqDto;
import com.ezcoins.project.otc.service.EzSellConfigService;
import com.ezcoins.response.Response;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.utils.BeanUtils;
import io.swagger.annotations.Api;
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
@Api(tags = "Admin-一键卖币配置")
@RequestMapping("/admin/otc/ezSellConfig")
public class EzSellConfigController {


    @Autowired
    private EzSellConfigService sellConfigService;

    @ApiOperation(value = "一键卖币配置")
    @PostMapping("sellConfig")
    @AuthToken
    public ResponseList<EzOneSellConfig> sellConfig() {
        return ResponseList.success(sellConfigService.list());
    }

    @NoRepeatSubmit
    @ApiOperation(value = "修改/添加一键卖币配置")
    @PostMapping("updateOrAddSellConfig")
    @AuthToken
    @Log(title = "OTC模块", logInfo ="修改/添加一键卖币配置", operatorType = OperatorType.MANAGE)
    public Response updateOrAddSellConfig(@RequestBody SellConfigReqDto sellConfigReqDto) {
        return sellConfigService.updateOrAddSellConfig(sellConfigReqDto);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "修改一键卖币配置状态")
    @PostMapping("updateSellConfigStatus")
    @AuthToken
    @Log(title = "OTC模块", logInfo ="修改一键卖币配置状态", operatorType = OperatorType.MANAGE)
    public Response updateSellConfigStatus(@RequestBody StatusReqDto statusReqDto) {
        LambdaQueryWrapper<EzOneSellConfig> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzOneSellConfig::getId,statusReqDto.getId());
        EzOneSellConfig one = sellConfigService.getOne(lambdaQueryWrapper);
        if ("0".equals(statusReqDto.getStatus())){
            LambdaUpdateWrapper<EzOneSellConfig> q=new LambdaUpdateWrapper<>();
            q.eq(EzOneSellConfig::getCoinName, one.getCoinName());
            q.eq(EzOneSellConfig::getStatus, "0");
            q.set(EzOneSellConfig::getStatus, "1");
            sellConfigService.update(q);
        }
        one.setStatus(statusReqDto.getStatus());
        sellConfigService.updateById(one);
        return Response.success();
    }


}

