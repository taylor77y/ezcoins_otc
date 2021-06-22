package com.ezcoins.project.otc.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzPaymentInfo;
import com.ezcoins.project.otc.entity.EzPaymentMethod;
import com.ezcoins.project.otc.entity.req.PaymentMethodReqDto;
import com.ezcoins.project.otc.service.EzPaymentInfoService;
import com.ezcoins.project.otc.service.EzPaymentMethodService;
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
 * 用户 支付宝信息 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-15
 */
@RestController
@Api(tags = "Admin-收款方式模块")
@RequestMapping("/admin/otc/paymentMethod")
public class PaymentMethodController {

    @Autowired
    private EzPaymentInfoService paymentInfoService;

    @Autowired
    private EzPaymentMethodService methodService;


    @ApiOperation(value = "收款类型  列表")
    @PostMapping("paymentMethodList")
    @AuthToken
    public ResponsePageList<EzPaymentMethod> paymentMethodList(@RequestBody SearchModel<EzPaymentMethod> searchModel) {
        return ResponsePageList.success(methodService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @NoRepeatSubmit
    @ApiOperation(value = "添加/修改  收款类型")
    @PostMapping("addOrUpdatePaymentMethod")
    @AuthToken
    @Log(title = "添加/修改  收款方式", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public BaseResponse addOrUpdatePaymentMethod(@RequestBody PaymentMethodReqDto paymentMethodReqDto) {
        methodService.addOrUpdatePaymentMethod(paymentMethodReqDto);
        return BaseResponse.success();
    }

    @ApiOperation(value = "收款方式信息列表")
    @PostMapping("paymentInfoList")
    @AuthToken
    public ResponsePageList<EzPaymentInfo> paymentInfoList(@RequestBody SearchModel<EzPaymentInfo> searchModel) {
        return ResponsePageList.success(paymentInfoService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }


}

