package com.ezcoins.project.consumer.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzAdvertisingApprove;
import com.ezcoins.project.consumer.entity.EzAdvertisingConfig;
import com.ezcoins.project.consumer.service.EzAdvertisingApproveService;
import com.ezcoins.project.consumer.service.EzAdvertisingConfigService;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * <p>
 * 高级认证设置 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-30
 */
@RestController
@Api(tags = "Admin-高级认证配置")
@RequestMapping("/admin/consumer/ezAdvertisingConfig")
public class EzAdvertisingConfigController {

    @Autowired
    private EzAdvertisingConfigService advertisingConfigService;

    @ApiOperation(value = "高级认证设置")
    @PostMapping("advertisingConfig")
    @AuthToken
    public Response<EzAdvertisingConfig> advertisingConfig() {
        return Response.success(advertisingConfigService.getById(1));
    }

    @ApiOperation(value = "修改高级认证配置")
    @PostMapping("update")
    @AuthToken
    @ApiImplicitParams({
            @ApiImplicitParam(name = "minMargin", value = "最低保证金", required = true),
    })
    @Log(title = "修改高级认证配置",businessType = BusinessType.UPDATE,operatorType= OperatorType.MANAGE)
    public BaseResponse update(HashMap<String,Object> params) {
        String minMargin1 = (String) params.get("minMargin");
        if (StringUtils.isEmpty(minMargin1)){
            return BaseResponse.error();
        }
        BigDecimal minMargin = new BigDecimal(minMargin1);
        LambdaUpdateWrapper<EzAdvertisingConfig> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.set(EzAdvertisingConfig::getMinMargin,minMargin);
        updateWrapper.set(EzAdvertisingConfig::getUpdateBy, ContextHandler.getUserName());
        updateWrapper.eq(EzAdvertisingConfig::getId,1);
        advertisingConfigService.update(null,updateWrapper);
        return BaseResponse.success();
    }
}

