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


    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "完善otc交易信息")
    @PostMapping("otcSetting")
    @Log(title = "完善otc交易信息", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse otcSetting(@RequestBody OtcSettingReqDto otcSettingReqDto) {
        return businessService.otcSetting(otcSettingReqDto);
    }

    @ApiOperation(value = "OTC 交易信息")
    @PostMapping({"advertisingBusiness/{userId}","advertisingBusiness"})
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
            return Response.error(MessageUtils.message("请先完善otc交易信息"));
        }
        AdvertisingBusinessInfoRespDto advertisingBusinessInfoRespDto = new AdvertisingBusinessInfoRespDto();
        BeanUtils.copyBeanProp(advertisingBusinessInfoRespDto,one);
        advertisingBusinessInfoRespDto.setKycStatus(kycStatus);
        advertisingBusinessInfoRespDto.setAdvertisingStatus(level);
        advertisingBusinessInfoRespDto.setRegistrationTime(createTime);
        //三十天成单
        LambdaQueryWrapper<EzOtcOrderMatch> q=new LambdaQueryWrapper<EzOtcOrderMatch>();
        q.eq(EzOtcOrderMatch::getOtcOrderUserId,userId1);
        q.eq(EzOtcOrderMatch::getStatus,MatchOrderStatus.COMPLETED);
        Date ndayStart = DateUtils.getNdayStart(-30);
        q.gt(EzOtcOrderMatch::getFinishTime,ndayStart);
        q.lt(EzOtcOrderMatch::getFinishTime,DateUtils.getNowDate());
        advertisingBusinessInfoRespDto.setMouthCount(orderMatchService.count());

        return Response.success(advertisingBusinessInfoRespDto);
    }


    @AuthToken
    @ApiOperation(value = "收款方式 列表")
    @GetMapping("paymentInfoList")
    public ResponseList<PaymentMethodRespDto> paymentMethodList() {
        String userId = ContextHandler.getUserId();
        LambdaQueryWrapper<EzPaymentInfo> alipayQueryWrapper = new LambdaQueryWrapper<>();
        alipayQueryWrapper.eq(EzPaymentInfo::getUserId, userId);
        List<EzPaymentMethod> list = methodService.list();
        ArrayList<PaymentMethodRespDto> respDtos = new ArrayList<>();
        paymentInfoService.list(alipayQueryWrapper).forEach(e -> {
            PaymentMethodRespDto paymentMethodRespDto = new PaymentMethodRespDto();
            paymentMethodRespDto.setPaymentQrCode(e.getPaymentQrCode());
            paymentMethodRespDto.setAccountNumber(e.getAccountNumber());
            paymentMethodRespDto.setRealName(e.getRealName());
            paymentMethodRespDto.setStatus(e.getStatus());
            paymentMethodRespDto.setPaymentMethodId(e.getPaymentMethodId());
            paymentMethodRespDto.setId(e.getId());
            EzPaymentMethod ezPaymentMethod = list.get(e.getPaymentMethodId() - 1);
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
    public BaseResponse updateOrAddPaymentInfo(@RequestBody PaymentQrcodeTypeReqDto qrcodeTypeReqDto) {
        return paymentInfoService.alipayPaymentMethod(qrcodeTypeReqDto);
    }

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "删除 收款方式")
    @PostMapping("deletePaymentInfo/{id}")
    @Log(title = "删除 收款方式", businessType = BusinessType.DELETE, operatorType = OperatorType.MOBILE)
    public BaseResponse deletePaymentInfo(@PathVariable String id) {
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
    public BaseResponse deletePaymentInfo(@RequestBody HashMap<String, String> map) {
        String securityPassword = map.get("securityPassword");
        String encode = EncoderUtil.encode(securityPassword);
        LambdaQueryWrapper<EzAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAdvertisingBusiness::getUserId, ContextHandler.getUserId());
        queryWrapper.eq(EzAdvertisingBusiness::getSecurityPassword, encode);
        int count = advertisingBusinessService.count(queryWrapper);
        if (count != 0) {
            throw new SecurityPasswordNotMatchException();
        }
        return BaseResponse.success();
    }

    @NoRepeatSubmit
    @ApiOperation(value = "发布 广告订单")
    @PostMapping("releaseAdvertisingOrder")
    @AuthToken(advertisingStatus = true)
    @Log(title = "发布 广告订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse releaseAdvertisingOrder(@RequestBody OtcOrderReqDto otcOrderReqDto) {
        return otcOrderService.releaseAdvertisingOrder(otcOrderReqDto);
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

    @ApiOperation(value = "购买 查询订单详情")
    @GetMapping("orderInfo/{otcOrderNo}")
    @AuthToken
    public Response<OrderInfo> orderInfo(@PathVariable String otcOrderNo) {
        return otcOrderService.orderInfo(otcOrderNo);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "用户 根据订单号下单 购买/出售")
    @PostMapping("placeAnOrder")
    @AuthToken(kyc = true)
    @Log(title = "下单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response<PaymentDetails> placeAnOrder(@RequestBody PlaceOrderReqDto placeOrderReqDto) {
        return otcOrderService.placeAnOrder(placeOrderReqDto);
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
    @ApiOperation(value = "商户 接单/拒接 (订单广告)")
    @PutMapping("operateOrderAd")
    @AuthToken
    @Log(title = "商户 接单(订单广告)", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse operateOrderAd(@RequestBody OrderOperateReqDto orderOperateReqDto) {
        return otcOrderService.merchantOrder(orderOperateReqDto);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "用户 取消订单（两个状态可取消订单  1：接单广告（卖家未接受订单）用户免费取消 2：接单广告/普通广告（用户未支付状态） 用户取消次数增加）")
    @PutMapping("cancelOrder/{matchOrderNo}")
    @AuthToken
    @Log(title = "用户 取消订单", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse cancelOrder(@PathVariable String matchOrderNo) {
        return orderMatchService.cancelOrder(matchOrderNo);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "根据用户id 查询订单")
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


    @NoRepeatSubmit
    @ApiOperation(value = "买家确认 付款")
    @PutMapping("confirmPayment/{matchOrderNo}")
    @AuthToken
    @Log(title = "买家确认 付款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse confirmPayment(@PathVariable String matchOrderNo) {
        return orderMatchService.confirmPayment(matchOrderNo);
    }


    @NoRepeatSubmit
    @ApiOperation(value = "卖家 放款")
    @PutMapping("sellerPut/{matchOrderNo}")
    @AuthToken
    @Log(title = "卖家 放款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse sellerPut(@PathVariable String matchOrderNo) {
        return orderMatchService.sellerPut(matchOrderNo);
    }


    @NoRepeatSubmit
    @ApiOperation(value = "订单申诉")
    @PutMapping("appeal")
    @AuthToken
    @Log(title = "订单申诉", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public BaseResponse appeal() {
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
        LambdaQueryWrapper<EzOtcChatMsg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcChatMsg::getOrderMatchNo, orderMatchNo);
        return ResponseList.success(otcChatMsgService.list(queryWrapper));
    }


    @ApiOperation(value = "根据订单号查询支付详情")
    @GetMapping("paymentInfo/{orderMatchNo}/{paymentMethodId}")
    @AuthToken
    public Response<PaymentMethodRespDto> paymentInfo(@PathVariable String orderMatchNo, @PathVariable String paymentMethodId) {
        PaymentMethodRespDto paymentMethodRespDto = new PaymentMethodRespDto();

        EzOtcOrderMatch orderMatch = orderMatchService.getById(orderMatchNo);

        LambdaQueryWrapper<EzPaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzPaymentInfo::getUserId, orderMatch.getOtcOrderUserId());
        queryWrapper.eq(EzPaymentInfo::getPaymentMethodId, paymentMethodId);
        EzPaymentInfo one = paymentInfoService.getOne(queryWrapper);
        EzPaymentMethod paymentMethod = methodService.getById(one.getPaymentMethodId());

        if (one.getPaymentMethodId().equals(PaymentMethod.BANK.getCode())) {
            paymentMethodRespDto.setBankName(one.getBankName());
        } else {
            paymentMethodRespDto.setPaymentQrCode(one.getPaymentQrCode());
        }
        paymentMethodRespDto.setRealName(one.getRealName());
        paymentMethodRespDto.setIcon(paymentMethod.getIcon());
        paymentMethodRespDto.setAccountNumber(one.getAccountNumber());
        paymentMethodRespDto.setPaymentMethodId(one.getPaymentMethodId());

        //改变支付方式
        orderMatch.setPaymentInfoId(one.getId());
        orderMatchService.updateById(orderMatch);
        return Response.success(paymentMethodRespDto);
    }

    @ApiOperation(value = "一键卖币(只支持人民币)")
    @PostMapping("sellOneKey")
    @AuthToken
    @Log(title = "一键卖币", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public BaseResponse sellOneKey(@RequestBody SellOneKeyReqDto sellOneKeyReqDto) {
        return orderMatchService.sellOneKey(sellOneKeyReqDto);
    }


    @ApiOperation(value = "广告订单匹配订单")
    @PostMapping("adMatchOrder")
    @AuthToken
    public ResponseList<OrderRecordRespDto> adMatchOrder(@RequestBody AdMatchOrderQueryReqDto matchOrderQueryReqDto) {
        Page<EzOtcOrderMatch> page = new Page<>(matchOrderQueryReqDto.getPage(), matchOrderQueryReqDto.getLimit());

        LambdaQueryWrapper<EzOtcOrderMatch> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderMatch::getOtcOrderUserId, ContextHandler.getUserId());

        String orderNo = matchOrderQueryReqDto.getOrderNo();
        if (StringUtils.isNotEmpty(orderNo)) {
            queryWrapper.eq(EzOtcOrderMatch::getOrderNo, orderNo);
        }
        String status = matchOrderQueryReqDto.getStatus();
        if ("1".equals(status)) {//已处理订单
            queryWrapper
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.COMPLETED.getCode()).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.ORDERBEENCANCELLED).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.COMPLETED.getCode()).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.REFUSE.getCode());
        }
        if ("2".equals(status)) {//未处理
            queryWrapper
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode()).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PAID.getCode()).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode());
        }
        Page<EzOtcOrderMatch> matchPage = orderMatchService.page(page, queryWrapper);
        List<EzOtcOrderMatch> records = matchPage.getRecords();

        List<OrderRecordRespDto> orderRecordRespDtos = new ArrayList<>();
        records.forEach(e -> {
            OrderRecordRespDto orderRespDto = new OrderRecordRespDto();
            BeanUtils.copyBeanProp(orderRespDto,e);
            orderRespDto.setPaymentInfo(paymentInfoService.getById(e.getPaymentInfoId()));
            orderRecordRespDtos.add(orderRespDto);
        });
        return ResponseList.success(orderRecordRespDtos);
    }


}
