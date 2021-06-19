package com.ezcoins.project.otc.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.EzOtcOrder;
import com.ezcoins.project.otc.entity.req.PaymentQrcodeTypeReqDto;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.project.otc.service.EzOtcOrderService;
import com.ezcoins.response.BaseResponse;
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
 * @since 2021-06-17
 */
@RestController
@Api(tags = "Admin-OTC订单模块")
@RequestMapping("/admin/otc/ezOtcOrder")
public class EzOtcOrderController {

    @Autowired
    private EzOtcOrderService otcOrderService;

    @AuthToken
    @ApiOperation(value = "OTC 订单列表")
    @PostMapping("otcOrderList")
    public ResponsePageList<EzOtcOrder> otcOrderList(@RequestBody SearchModel<EzOtcOrder> searchModel){
        return ResponsePageList.success(otcOrderService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }







}

