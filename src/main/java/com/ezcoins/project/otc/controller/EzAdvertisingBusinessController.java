package com.ezcoins.project.otc.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.EzPaymentMethod;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.ezcoins.project.otc.service.EzPaymentMethodService;
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
 *  前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
@RestController
@Api(tags = "Admin-OTC广告商户模块")
@RequestMapping("/admin/otc/ezAdvertisingBusiness")
public class EzAdvertisingBusinessController {

    @Autowired
    private EzAdvertisingBusinessService advertisingBusinessService;


    @ApiOperation(value = "OTC广告商户  列表")
    @PostMapping("advertisingBusinessList")
    @AuthToken
    public ResponsePageList<EzAdvertisingBusiness> advertisingBusinessList(@RequestBody SearchModel<EzAdvertisingBusiness> searchModel) {
        return ResponsePageList.success(advertisingBusinessService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }






















}

