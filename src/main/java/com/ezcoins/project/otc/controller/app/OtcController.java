package com.ezcoins.project.otc.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.user.SecurityPasswordNotMatchException;
import com.ezcoins.project.otc.entity.*;
import com.ezcoins.project.otc.entity.req.*;
import com.ezcoins.project.otc.entity.resp.PaymentMethodRespDto;
import com.ezcoins.project.otc.service.*;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.EncoderUtil;
import com.ezcoins.utils.Md5Util;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private EzPaymentInfoService paymentInfoService;

    @Autowired
    private EzPaymentMethodService methodService;

    @Autowired
    private EzAdvertisingBusinessService advertisingBusinessService;

    @Autowired
    private EzOtcOrderService otcOrderService;

    @Autowired
    private EzOtcOrderMatchService orderMatchService;

    @Autowired
    private EzAdvertisingBusinessService businessService;

    @Autowired
    private EzOtcChatMsgService otcChatMsgService;

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "完善otc交易信息")
    @PostMapping("otcSetting")
    @Log(title = "完善otc交易信息", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse otcSetting(@RequestBody OtcSettingReqDto otcSettingReqDto){
        return businessService.otcSetting(otcSettingReqDto);
    }


    @ApiOperation(value = "OTC 交易信息")
    @PostMapping("advertisingBusiness/{userId}")
    @AuthToken
    public Response<EzAdvertisingBusiness> advertisingBusiness(@PathVariable(value = "userId",required = false) String userId){
        String userId1=StringUtils.isNotEmpty(userId) ? userId : ContextHandler.getUserId();
        LambdaQueryWrapper<EzAdvertisingBusiness> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId,userId1);
        EzAdvertisingBusiness one = advertisingBusinessService.getOne(lambdaQueryWrapper);
        if (one==null && StringUtils.isEmpty(userId)){
            return Response.error("请先完善otc交易信息");
        }
        return Response.success(one);
    }

    @AuthToken
    @ApiOperation(value = "收款方式 列表")
    @GetMapping("paymentInfoList")
    public ResponseList<PaymentMethodRespDto> paymentMethodList(){
        String userId = ContextHandler.getUserId();
        LambdaQueryWrapper<EzPaymentInfo> alipayQueryWrapper=new LambdaQueryWrapper<>();
        alipayQueryWrapper.eq(EzPaymentInfo::getUserId,userId);
        List<EzPaymentMethod> list = methodService.list();

        ArrayList<PaymentMethodRespDto> respDtos = new ArrayList<>();
        paymentInfoService.list(alipayQueryWrapper).forEach(e->{
            PaymentMethodRespDto paymentMethodRespDto = new PaymentMethodRespDto();
            paymentMethodRespDto.setPaymentQrCode(e.getPaymentQrCode());
            paymentMethodRespDto.setAccountNumber(e.getAccountNumber());
            paymentMethodRespDto.setRealName(e.getRealName());
            paymentMethodRespDto.setStatus(e.getStatus());
            EzPaymentMethod ezPaymentMethod = list.get(e.getPaymentMethodId());
            paymentMethodRespDto.setIcon(ezPaymentMethod.getIcon());
            paymentMethodRespDto.setBankName(e.getBankName());
            respDtos.add(paymentMethodRespDto);
        });
        return ResponseList.success(respDtos);
    }

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "添加/修改  收款方式")
    @PostMapping("updateOrAddPaymentInfo")
    @Log(title = "添加 收款方式", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse updateOrAddPaymentInfo(@RequestBody PaymentQrcodeTypeReqDto qrcodeTypeReqDto){
        return paymentInfoService.alipayPaymentMethod(qrcodeTypeReqDto);
    }

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "删除 收款方式")
    @PostMapping("deletePaymentInfo/{id}")
    @Log(title = "删除 收款方式", businessType = BusinessType.DELETE, operatorType = OperatorType.MOBILE)
    public BaseResponse deletePaymentInfo(@PathVariable String id){
        paymentInfoService.removeById(id);
        return BaseResponse.success();
    }

    @NoRepeatSubmit
    @AuthToken
    @ApiImplicitParams({
            @ApiImplicitParam(name = "securityPassword", value = "安全密码", required = true),
    })
    @ApiOperation(value = "判断安全密码是否正确")
    @PostMapping("deletePaymentInfo")
    @Log(title = "删除 收款方式", businessType = BusinessType.DELETE, operatorType = OperatorType.MOBILE)
    public BaseResponse deletePaymentInfo(@RequestBody HashMap<String,String> map){
        String securityPassword = map.get("securityPassword");
        String encode = EncoderUtil.encode(securityPassword);
        LambdaQueryWrapper<EzAdvertisingBusiness> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAdvertisingBusiness::getUserId,ContextHandler.getUserId());
        queryWrapper.eq(EzAdvertisingBusiness::getSecurityPassword,encode);
        int count = advertisingBusinessService.count(queryWrapper);
        if (count!=0){
            throw new SecurityPasswordNotMatchException();
        }
        return BaseResponse.success();
    }


    @NoRepeatSubmit
    @ApiOperation(value = "发布 广告订单")
    @PostMapping("releaseAdvertisingOrder")
    @AuthToken
    @Log(title = "发布 广告订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse releaseAdvertisingOrder(@RequestBody OtcOrderReqDto otcOrderReqDto){
        return otcOrderService.releaseAdvertisingOrder(otcOrderReqDto);
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
    @ApiOperation(value = "买家确认 付款")
    @PutMapping("confirmPayment/{matchOrderNo}")
    @AuthToken
    @Log(title = "买家确认 付款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse confirmPayment(@PathVariable String matchOrderNo){
        return  orderMatchService.confirmPayment(matchOrderNo);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "卖家 放款")
    @PutMapping("sellerPut/{matchOrderNo}")
    @AuthToken
    @Log(title = "卖家 放款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse sellerPut(@PathVariable String matchOrderNo){
        return  orderMatchService.sellerPut(matchOrderNo);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "订单申诉")
    @PutMapping("appeal")
    @AuthToken
    @Log(title = "订单申诉", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse appeal(){
        //TODO:  otcOrderService.sellerPut(orderOperateReqDto);
        return BaseResponse.success();
    }


    @ApiOperation(value = "接受 发送聊天记录")
    @PostMapping("sendChat")
    @AuthToken
    public BaseResponse sendChat(@RequestBody ChatMsgReqDto msgReqDto) {
       return otcChatMsgService.sendChat(msgReqDto);
    }



    @ApiOperation(value = "根据 匹配订单id查询聊天记录")
    @GetMapping("chatMsg/{orderMatchNo}")
    @AuthToken
    public ResponseList<EzOtcChatMsg> advertisingBusinessList(@PathVariable String orderMatchNo) {
        LambdaQueryWrapper<EzOtcChatMsg> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcChatMsg::getOrderMatchNo,orderMatchNo);
        return ResponseList.success(otcChatMsgService.list(queryWrapper));
    }

}
