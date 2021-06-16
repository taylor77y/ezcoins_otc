package com.ezcoins.project.otc.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.EzPaymentMethod;
import com.ezcoins.project.otc.entity.EzPaymentQrcode;
import com.ezcoins.project.otc.entity.EzPaymentBank;
import com.ezcoins.project.otc.entity.req.PaymentMethodReqDto;
import com.ezcoins.project.otc.service.EzPaymentMethodService;
import com.ezcoins.project.otc.service.EzPaymentQrcodeService;
import com.ezcoins.project.otc.service.EzPaymentBankService;
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
    private EzPaymentQrcodeService qrcodeService;
    @Autowired
    private EzPaymentBankService bankService;
    @Autowired
    private EzPaymentMethodService methodService;


    @ApiOperation(value = "收款方式  列表")
    @PostMapping("paymentMethodList")
    @AuthToken
    public ResponsePageList<EzPaymentMethod> paymentMethodList(@RequestBody SearchModel<EzPaymentMethod> searchModel) {
        return ResponsePageList.success(methodService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @ApiOperation(value = "添加/修改  收款方式")
    @PostMapping("addOrUpdatePaymentMethod")
    @AuthToken
    public BaseResponse addOrUpdatePaymentMethod(@RequestBody PaymentMethodReqDto paymentMethodReqDto) {
        methodService.addOrUpdatePaymentMethod(paymentMethodReqDto);
        return BaseResponse.success();
    }

    @ApiOperation(value = "微信/支付宝..相同类型  收款方式信息列表")
    @PostMapping("paymentQrcodeList")
    @AuthToken
    public ResponsePageList<EzPaymentQrcode> paymentQrcodeList(@RequestBody SearchModel<EzPaymentQrcode> searchModel) {
        return ResponsePageList.success(qrcodeService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }


    @ApiOperation(value = "银行... 类型收款方式信息列表")
    @PostMapping("bankList")
    @AuthToken
    public ResponsePageList<EzPaymentBank> bankList(@RequestBody SearchModel<EzPaymentBank> searchModel) {
        return ResponsePageList.success(bankService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }


}

