package com.ezcoins.project.otc.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.EzPaymentQrcode;
import com.ezcoins.project.otc.entity.EzPaymentBank;
import com.ezcoins.project.otc.entity.req.*;
import com.ezcoins.project.otc.service.*;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/15 16:17
 * @Version:1.0
 */
@RestController
@Api(tags = "APP-OTC模块")
@RequestMapping("/otc/app")
public class OtcController {
    @Autowired
    private EzPaymentQrcodeService qrcodeService;

    @Autowired
    private EzPaymentBankService bankService;


    @Autowired
    private EzAdvertisingBusinessService advertisingBusinessService;


    @Autowired
    private EzOtcOrderService otcOrderService;

    @Autowired
    private EzOtcOrderMatchService orderMatchService;


    @AuthToken
    @ApiOperation(value = "收款方式 列表")
    @GetMapping("paymentMethodList")
    public Response<EzPaymentBank> paymentMethodList(){
        String userId = ContextHandler.getUserId();
        LambdaQueryWrapper<EzPaymentQrcode> alipayQueryWrapper=new LambdaQueryWrapper<>();
        alipayQueryWrapper.eq(EzPaymentQrcode::getUserId,userId);
        EzPaymentQrcode paymentQrcode = qrcodeService.getOne(alipayQueryWrapper);

        LambdaQueryWrapper<EzPaymentBank> bankQueryWrapper=new LambdaQueryWrapper<>();
        bankQueryWrapper.eq(EzPaymentBank::getUserId,userId);
        EzPaymentBank userBank = bankService.getOne(bankQueryWrapper);

//        PaymentMethodRespDto paymentMethodRespDto = new PaymentMethodRespDto();
//        paymentMethodRespDto.setEzUserAlipay(userAlipay);
//        paymentMethodRespDto.setEzUserWechat(userWechat);
//        paymentMethodRespDto.setEzPaymentBank(userBank);
        return Response.success(null);
    }

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "添加/修改 支付宝/微信 收款方式")
    @PostMapping("alipayWechatPaymentMethod")
    @Log(title = "添加 支付宝/微信 收款方式", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse alipayWechatPaymentMethod(@RequestBody PaymentQrcodeTypeReqDto qrcodeTypeReqDto){
        qrcodeService.alipayPaymentMethod(qrcodeTypeReqDto);
        return BaseResponse.success();
    }

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "添加/修改 银行收款方式")
    @PostMapping("bankPaymentMethod")
    @Log(title = "添加/修改 银行收款方式", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse bankPaymentMethod(@RequestBody BankReqDto bankReqDto){
        bankService.bankPaymentMethod(bankReqDto);
        return BaseResponse.success();
    }

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "删除 收款方式")
    @PostMapping("deletePaymentMethod")
    @Log(title = "删除 收款方式", businessType = BusinessType.DELETE, operatorType = OperatorType.MOBILE)
    public BaseResponse deletePaymentMethod(@RequestBody DpMethodReqDto dpMethodReqDto){

        return BaseResponse.success();
    }


    @ApiOperation(value = "OTC广告商户  信息")
    @PostMapping("advertisingBusiness/{userId}")
    @AuthToken
    public Response<EzAdvertisingBusiness> advertisingBusiness(@PathVariable(value = "userId",required = false) String userId){
        String userId1=StringUtils.isNotEmpty(userId) ? userId : ContextHandler.getUserId();
        LambdaQueryWrapper<EzAdvertisingBusiness> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId,userId1);
        return Response.success(advertisingBusinessService.getOne(lambdaQueryWrapper));
    }


    @NoRepeatSubmit
    @ApiOperation(value = "发布 广告订单")
    @PostMapping("releaseAdvertisingOrder")
    @AuthToken
    @Log(title = "发布 广告订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse releaseAdvertisingOrder(@RequestBody OtcOrderReqDto otcOrderReqDto){
        otcOrderService.releaseAdvertisingOrder(otcOrderReqDto);
        return BaseResponse.success();
    }


    @NoRepeatSubmit
    @ApiOperation(value = "用户 根据订单号下单 购买/出售")
    @PostMapping("placeAnOrder")
    @AuthToken
    @Log(title = "下单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse placeAnOrder(@RequestBody PlaceOrderReqDto placeOrderReqDto){
        return otcOrderService.placeAnOrder(placeOrderReqDto);
    }


    @NoRepeatSubmit
    @ApiOperation(value = "商户 下架广告订单")
    @PutMapping("offShelfOrder/{orderNo}")
    @AuthToken
    @Log(title = "商户 下架广告订单", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse offShelfOrder(@PathVariable String orderNo){
        return otcOrderService.offShelfOrder(orderNo);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "商户 接单/拒接 (订单广告)")
    @PutMapping("operateOrderAd")
    @AuthToken
    @Log(title = "商户 接单(订单广告)", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse operateOrderAd(@RequestBody OrderOperateReqDto orderOperateReqDto){
        return otcOrderService.merchantOrder(orderOperateReqDto);
    }


    @NoRepeatSubmit
    @ApiOperation(value = "用户 取消订单（两个状态可取消订单  1：接单广告（卖家未接受订单）用户免费取消 2：接单广告/普通广告（用户未支付状态） 用户取消次数增加）")
    @PutMapping("cancelOrder/{matchOrderNo}")
    @AuthToken
    @Log(title = "用户 取消订单", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse cancelOrder(@PathVariable String matchOrderNo){
        return orderMatchService.cancelOrder(matchOrderNo);
    }


    @NoRepeatSubmit
    @ApiOperation(value = "卖家 放款")
    @PutMapping("sellerPut")
    @AuthToken
    @Log(title = "卖家 放款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse sellerPut(@RequestBody OrderOperateReqDto orderOperateReqDto){
        return otcOrderService.merchantOrder(orderOperateReqDto);
    }


















}
