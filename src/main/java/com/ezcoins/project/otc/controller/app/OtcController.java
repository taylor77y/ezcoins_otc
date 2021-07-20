package com.ezcoins.project.otc.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.constant.enums.otc.PaymentMethod;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.user.SecurityPasswordNotMatchException;
import com.ezcoins.project.coin.entity.resp.AccountRespDto;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.*;
import com.ezcoins.project.otc.entity.req.*;
import com.ezcoins.project.otc.entity.resp.*;
import com.ezcoins.project.otc.service.*;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private EzUserService userService;

    @Autowired
    private EzOtcOrderPaymentService orderPaymentService;

    @Autowired
    private EzOtcOrderAppealService appealService;

    @Autowired
    private EzSellConfigService ezSellConfigService;

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "完善otc交易信息")
    @PostMapping("otcSetting")
    @Log(title = "完善otc交易信息", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse otcSetting(@RequestBody OtcSettingReqDto otcSettingReqDto) {
        return businessService.otcSetting(otcSettingReqDto);
    }

    @GetMapping("isUpdateAdName")
    @ApiOperation(value = "判断是否修改过昵称")
    @AuthToken
    public BaseResponse isUpdateAdName() {
        String userId = ContextHandler.getUserId();
        LambdaQueryWrapper<EzAdvertisingBusiness> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, userId);
        EzAdvertisingBusiness one = advertisingBusinessService.getOne(lambdaQueryWrapper);
        if (one.getAdvertisingName().equals(userId)) {
            return BaseResponse.error();
        } else {
            return BaseResponse.success();
        }
    }

    @ApiOperation(value = "OTC 交易信息")
    @PostMapping({"advertisingBusiness/{userId}", "advertisingBusiness"})
    @AuthToken
    public Response<AdvertisingBusinessInfoRespDto> advertisingBusiness(@PathVariable(value = "userId", required = false) String userId) {
        String userId2 = ContextHandler.getUserId();
        String userId1 = StringUtils.isNotEmpty(userId) ? userId : userId2;
        LambdaQueryWrapper<EzAdvertisingBusiness> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, userId1);
        EzAdvertisingBusiness one = advertisingBusinessService.getOne(lambdaQueryWrapper);
        EzUser user = userService.getById(userId1);
        String kycStatus = user.getKycStatus();
        Date createTime = user.getCreateTime();
        String level = user.getLevel();
        if (StringUtils.isEmpty(userId) && one.getAdvertisingName().equals(userId2)) {
            return Response.error(MessageUtils.message("请先完善otc交易信息"), 700);
        }
        AdvertisingBusinessInfoRespDto advertisingBusinessInfoRespDto = new AdvertisingBusinessInfoRespDto();
        BeanUtils.copyBeanProp(advertisingBusinessInfoRespDto, one);
        advertisingBusinessInfoRespDto.setKycStatus(kycStatus);
        advertisingBusinessInfoRespDto.setAdvertisingStatus(level);
        advertisingBusinessInfoRespDto.setRegistrationTime(createTime);
        advertisingBusinessInfoRespDto.setMargin(one.getMargin());
        //三十天成单
        LambdaQueryWrapper<EzOtcOrderMatch> q = new LambdaQueryWrapper<EzOtcOrderMatch>();
        q.eq(EzOtcOrderMatch::getOtcOrderUserId, userId1);
        q.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.COMPLETED);
        Date ndayStart = DateUtils.getNdayStart(-30);
        q.gt(EzOtcOrderMatch::getFinishTime, ndayStart);
        q.lt(EzOtcOrderMatch::getFinishTime, DateUtils.getNowDate());
        advertisingBusinessInfoRespDto.setMouthCount(orderMatchService.count(q));
        return Response.success(advertisingBusinessInfoRespDto);
    }

    @AuthToken(kyc = true)
    @ApiOperation(value = "收款方式 列表")
    @GetMapping("paymentInfoList")
    public ResponseList<EzPaymentInfo> paymentMethodList() {
        LambdaQueryWrapper<EzPaymentInfo> alipayQueryWrapper = new LambdaQueryWrapper<>();
        alipayQueryWrapper.eq(EzPaymentInfo::getUserId, ContextHandler.getUserId());
        return ResponseList.success(paymentInfoService.list(alipayQueryWrapper));
    }

    @NoRepeatSubmit
    @AuthToken(kyc = true)
    @ApiOperation(value = "添加/修改收款方式")
    @PostMapping("updateOrAddPaymentInfo")
    @Log(title = "添加收款方式", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse updateOrAddPaymentInfo(@RequestBody PaymentQrcodeTypeReqDto qrcodeTypeReqDto) {
        return paymentInfoService.alipayPaymentMethod(qrcodeTypeReqDto);
    }
    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "删除收款方式")
    @PostMapping("deletePaymentInfo/{id}")
    @Log(title = "删除 收款方式", businessType = BusinessType.DELETE, operatorType = OperatorType.MOBILE)
    public BaseResponse deletePaymentInfo(@PathVariable String id) {
        paymentInfoService.removeById(id);
        return BaseResponse.success();
    }

    @AuthToken
    @ApiImplicitParams({
            @ApiImplicitParam(name = "securityPassword", value = "安全密码", required = true),
    })
    @ApiOperation(value = "判断安全密码是否正确")
    @PostMapping("checkSecurityPassword")
    public BaseResponse checkSecurityPassword(@RequestBody HashMap<String, String> map) {
        String securityPassword = map.get("securityPassword");
        LambdaQueryWrapper<EzAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAdvertisingBusiness::getUserId, ContextHandler.getUserId());
        EzAdvertisingBusiness businessServiceOne = advertisingBusinessService.getOne(queryWrapper);
        if (!EncoderUtil.matches(securityPassword,businessServiceOne.getSecurityPassword())) {
            throw new SecurityPasswordNotMatchException();
        }
        return BaseResponse.success();
    }

   //    -----------------------------------------------------------------------------------------------------------
    @NoRepeatSubmit
    @ApiOperation(value = "发布广告订单")
    @PostMapping("releaseAdvertisingOrder")
    @AuthToken(kyc = true)
    @Log(title = "发布广告订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse releaseAdvertisingOrder(@RequestBody OtcOrderReqDto otcOrderReqDto) {
        otcOrderReqDto.setUserId(ContextHandler.getUserId());
        return otcOrderService.releaseAdvertisingOrder(otcOrderReqDto);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "用户根据订单号下单购买/出售")
    @PostMapping("placeAnOrder")
    @AuthToken(kyc = true)
    @Log(title = "下单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response<PaymentDetails> placeAnOrder(@RequestBody PlaceOrderReqDto placeOrderReqDto) {
        return otcOrderService.placeAnOrder(placeOrderReqDto);
    }

    @ApiOperation(value = "订单 列表")
    @PostMapping("otcOrderList")
    @AuthToken
    public ResponseList<OtcOrderRespDto> otcOrderList(@RequestBody OtcOrderQueryReqDto orderQueryReqDto) {
        return otcOrderService.otcOrderList(orderQueryReqDto);
    }


    @ApiOperation(value = "NEW ORDER")
    @PostMapping("newOrderList")
    @AuthToken
    public ResponseList<NewOrderRespDto> nowOrderList(@RequestBody PageQuery pageQuery) {
        return otcOrderService.nowOrderList(pageQuery);
    }


    @NoRepeatSubmit
    @ApiOperation(value = "商户 下架广告订单")
    @PutMapping("offShelfOrder/{orderNo}")
    @AuthToken
    @Log(title = "商户 下架广告订单", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse offShelfOrder(@PathVariable String orderNo) {
        return otcOrderService.offShelfOrder(orderNo);
    }


    @NoRepeatSubmit
    @ApiOperation(value = "商户接单/拒接 (订单广告)")
    @PutMapping("operateOrderAd")
    @AuthToken
    @Log(title = "商户接单(订单广告)", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse operateOrderAd(@RequestBody OrderOperateReqDto orderOperateReqDto) {
        return otcOrderService.merchantOrder(orderOperateReqDto);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "用户 取消订单（两个状态可取消订单  1：接单广告（卖家未接受订单）用户免费取消 " +
            "2：接单广告/普通广告（用户未支付状态） 用户取消次数增加）")
    @PutMapping("cancelOrder/{matchOrderNo}")
    @AuthToken
    @Log(title = "用户 取消订单", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse cancelOrder(@PathVariable String matchOrderNo) {
        return orderMatchService.cancelOrder(matchOrderNo);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "买家确认付款")
    @PutMapping("confirmPayment/{matchOrderNo}")
    @AuthToken
    @Log(title = "买家确认付款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse confirmPayment(@PathVariable String matchOrderNo) {
        return orderMatchService.confirmPayment(matchOrderNo);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "卖家 放款")
    @PutMapping("sellerPut/{matchOrderNo}")
    @AuthToken
    @Log(title = "卖家放款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse sellerPut(@PathVariable String matchOrderNo) {
        return orderMatchService.sellerPut(matchOrderNo,false);
    }


    //    --------------------------------------------------订单记录详情
    @ApiOperation(value = "购买查询订单详情")
    @GetMapping("orderInfo/{otcOrderNo}")
    @AuthToken
    public Response<OrderInfo> orderInfo(@PathVariable String otcOrderNo) {
        return otcOrderService.orderInfo(otcOrderNo);
    }


    @ApiOperation(value = "根据用户id查询订单列表")
    @GetMapping("otcOrderListBy/{userId}")
    @AuthToken
    public Response<OtcInfoOrder> otcOrderListBy(@PathVariable String userId) {
        return orderMatchService.otcOrderListBy(userId);
    }


    @ApiOperation(value = "发布订单列表")
    @PostMapping({"releaseOrderList/{userId}", "releaseOrderList"})
    @AuthToken
    public ResponseList<ReleaseOrderRespDto> releaseOrderList(@PathVariable(required = false) String userId) {
        LambdaQueryWrapper<EzOtcOrder> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isEmpty(userId)) {
            queryWrapper.eq(EzOtcOrder::getUserId, ContextHandler.getUserId());
        } else {
            queryWrapper.eq(EzOtcOrder::getUserId, userId);
        }
        queryWrapper.eq(EzOtcOrder::getStatus, 0);
        List<EzOtcOrder> list = otcOrderService.list(queryWrapper);
        List<ReleaseOrderRespDto> releaseOrderRespDtos = new ArrayList<>();
        ReleaseOrderRespDto releaseOrderRespDto = new ReleaseOrderRespDto();
        List<EzPaymentMethod> methods = methodService.list();
        list.forEach(e -> {
            List<Integer> paymentMethods = new ArrayList<>();
            if (StringUtils.isNotNull(e.getPaymentMethod1())) {
                paymentMethods.add(methods.get(e.getPaymentMethod1() - 1).getId());
            }
            if (StringUtils.isNotNull(e.getPaymentMethod2())) {
                paymentMethods.add(methods.get(e.getPaymentMethod2() - 1).getId());
            }
            if (StringUtils.isNotNull(e.getPaymentMethod3())) {
                paymentMethods.add(methods.get(e.getPaymentMethod3() - 1).getId());
            }
            releaseOrderRespDto.setPaymentMethods(paymentMethods);
            releaseOrderRespDto.setCoinName(e.getCoinName());
            releaseOrderRespDto.setOrderNo(e.getOrderNo());
            releaseOrderRespDto.setMaximumLimit(e.getMaximumLimit());
            releaseOrderRespDto.setMinimumLimit(e.getMinimumLimit());
            releaseOrderRespDto.setPrice(e.getPrice());
            releaseOrderRespDto.setType(e.getType());
            releaseOrderRespDto.setIsAdvertising(e.getIsAdvertising());
            releaseOrderRespDto.setLastAmount(e.getTotalAmount().subtract(e.getQuotaAmount()));
            releaseOrderRespDtos.add(releaseOrderRespDto);
        });
        return ResponseList.success(releaseOrderRespDtos);
    }


    @ApiOperation(value = "订单记录")
    @PostMapping("orderRecord")
    @AuthToken
    public ResponseList<OrderRecordRespDto> orderRecord(@RequestBody OrderRecordQueryReqDto orderRecordQueryReqDto) {
        return orderMatchService.orderRecord(orderRecordQueryReqDto);
    }


    @ApiOperation(value = "广告订单匹配订单")
    @PostMapping("adMatchOrder")
    @AuthToken
    public ResponseList<OrderRecordRespDto> adMatchOrder(@RequestBody AdMatchOrderQueryReqDto matchOrderQueryReqDto) {
        return orderMatchService.adMatchOrder(matchOrderQueryReqDto);
    }


    @ApiOperation(value = "一键卖币(只支持人民币)")
    @PostMapping("sellOneKey")
    @AuthToken(kyc = true)
    @Log(title = "一键卖币", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse sellOneKey(@RequestBody SellOneKeyReqDto sellOneKeyReqDto) {
        return orderMatchService.sellOneKey(sellOneKeyReqDto);
    }

    @ApiOperation(value = "一键卖币配置")
    @GetMapping("oneKeyConfig")
    public ResponseList<EzOneSellConfig> oneKeyConfig() {
        return ResponseList.success(ezSellConfigService.list());
    }


    @NoRepeatSubmit
    @ApiOperation(value = "订单申诉")
    @PutMapping("appeal")
    @AuthToken
    @Log(title = "订单申诉", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse appeal(@RequestBody AppealReqDto appealReqDto) {
       return appealService.appeal(appealReqDto);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "取消申诉")
    @PutMapping("cancelAppeal/{orderMatchNo}")
    @AuthToken
    @Log(title = "取消申诉", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse cancelAppeal(@PathVariable String orderMatchNo) {
        return appealService.cancelAppeal(orderMatchNo);
    }

    @ApiOperation(value = "根据订单号查询申诉详情")
    @AuthToken
    @Log(title = "根据订单号查询申诉详情", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    @GetMapping("appealInfo/{orderMatchNo}")
    public Response<EzOtcOrderAppeal> appealInfo(@PathVariable String orderMatchNo){
        LambdaQueryWrapper<EzOtcOrderAppeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderAppeal::getUserId,ContextHandler.getUserId());
        queryWrapper.eq(EzOtcOrderAppeal::getOrderMatchNo,orderMatchNo);
        return Response.success(appealService.getOne(queryWrapper));
    }

    //    --------------------------------------------------支付详情
    @ApiOperation(value = "根据上架订单号查询支付详情")
    @GetMapping("paymentOrderInfo/{orderNo}")
    @AuthToken
    public ResponseList<EzOtcOrderPayment> paymentOrderInfo(@PathVariable String orderNo) {
        LambdaQueryWrapper<EzOtcOrderPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzOtcOrderPayment::getOrderNo, orderNo);
        return ResponseList.success(orderPaymentService.list(lambdaQueryWrapper));
    }

    @ApiOperation(value = "根据订单号查询支付详情")
    @GetMapping("paymentInfo/{orderMatchNo}/{orderPaymentId}")
    @AuthToken
    public Response<OrderPaymentRspDto> paymentInfo(@PathVariable String orderMatchNo, @PathVariable String orderPaymentId) {
        OrderPaymentRspDto paymentMethodRespDto = new OrderPaymentRspDto();
        EzOtcOrderMatch orderMatch = orderMatchService.getById(orderMatchNo);
        if ("1".equals(orderMatch.getType())) {//卖
            EzOtcOrderPayment one = orderPaymentService.getById(orderPaymentId);
            BeanUtils.copyBeanProp(paymentMethodRespDto,one);
            orderMatch.setOrderPaymentId(orderPaymentId);
            orderMatchService.updateById(orderMatch);
        }
        return Response.success(paymentMethodRespDto);
    }


    @ApiOperation(value = "根据匹配订单号查询支付详情")
    @GetMapping("paymentMatchInfo/{orderMatchNo}")
    @AuthToken
    public ResponseList<EzOtcOrderPayment> paymentMatchInfo(@PathVariable String orderMatchNo) {
        LambdaQueryWrapper<EzOtcOrderPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzOtcOrderPayment::getOrderMatchNo, orderMatchNo);
        return ResponseList.success(orderPaymentService.list(lambdaQueryWrapper));
    }

    //    --------------------------------------------------聊天
    @ApiOperation(value = "接受 发送聊天记录")
    @PostMapping("sendChat")
    @AuthToken
    public BaseResponse sendChat(@RequestBody EzOtcChatMsg ezOtcChatMsg) {
        ezOtcChatMsg.setIsSystem("1");
        List<EzOtcChatMsg> msgReqDtos =new ArrayList<>();
        msgReqDtos.add(ezOtcChatMsg);
        return otcChatMsgService.sendChat(msgReqDtos, ContextHandler.getUserId());
    }

    @ApiOperation(value = "根据匹配订单id查询聊天记录")
    @GetMapping("chatMsg/{orderMatchNo}")
    @AuthToken
    public ResponseList<ChatMsgRespDto> chatMsg(@PathVariable String orderMatchNo) {
        return  otcChatMsgService.chatMsg(orderMatchNo);
    }

}
