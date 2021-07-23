package com.ezcoins.project.otc.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.constant.enums.otc.PaymentMethod;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.user.SecurityPasswordNotMatchException;
import com.ezcoins.project.coin.entity.resp.AccountRespDto;
import com.ezcoins.project.coin.service.TypeService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.*;
import com.ezcoins.project.otc.entity.req.*;
import com.ezcoins.project.otc.entity.resp.*;
import com.ezcoins.project.otc.service.*;
import com.ezcoins.response.Response;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
    @Autowired
    private EzUserKycService kycService;
    @Autowired
    private EzOtcConfigService configService;
    @Autowired
    private TypeService typeService;

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "完善otc交易信息")
    @PostMapping("otcSetting")
    @Log(title = "完善otc交易信息", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response otcSetting(@RequestBody OtcSettingReqDto otcSettingReqDto) {
        return businessService.otcSetting(otcSettingReqDto);
    }

    @GetMapping("isUpdateAd")
    @ApiOperation(value = "判断是否修改过")
    @AuthToken
    public Response isUpdateAd() {
        String userId = ContextHandler.getUserId();
        LambdaQueryWrapper<EzAdvertisingBusiness> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, userId);
        EzAdvertisingBusiness one = advertisingBusinessService.getOne(lambdaQueryWrapper);
        if (StringUtils.isNotEmpty(one.getSecurityPassword())) {
            return Response.success();
        } else {
            return Response.error();
        }
    }
    @GetMapping("getRealName")
    @ApiOperation(value = "查询真实姓名")
    @AuthToken
    public Response<HashMap> getRealName(){
        LambdaQueryWrapper<EzUserKyc> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzUserKyc::getUserId,ContextHandler.getUserId());
        EzUserKyc one = kycService.getOne(lambdaQueryWrapper);
        if (one==null){
            return Response.error(MessageUtils.message("请先完成实名认证"));
        }
        HashMap<String,String> map=new HashMap<>(1);
        map.put("realName",one.getLastName()+one.getFirstName());
        return Response.success(map);
    }


    @ApiOperation(value = "OTC交易信息")
    @PostMapping({"advertisingBusiness/{userId}", "advertisingBusiness"})
    @AuthToken
    public Response<AdvertisingBusinessInfoRespDto> advertisingBusiness(@PathVariable(value = "userId", required = false) String userId) {
        String userId2 = ContextHandler.getUserId();
        String userId1 = StringUtils.isNotEmpty(userId) ? userId : userId2;
        LambdaQueryWrapper<EzAdvertisingBusiness> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, userId1);
        EzAdvertisingBusiness one = advertisingBusinessService.getOne(lambdaQueryWrapper);
        if (StringUtils.isEmpty(userId) && StringUtils.isEmpty(one.getSecurityPassword())) {
            return Response.error(MessageUtils.message("请先完善otc交易信息"),700);
        }
        EzUser user = userService.getById(userId1);
        String kycStatus = user.getKycStatus();
        Date createTime = user.getCreateTime();
        String level = user.getLevel();
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
    public Response updateOrAddPaymentInfo(@RequestBody PaymentQrcodeTypeReqDto qrcodeTypeReqDto) {
        return paymentInfoService.alipayPaymentMethod(qrcodeTypeReqDto);
    }

    @NoRepeatSubmit
    @AuthToken
    @ApiOperation(value = "删除收款方式")
    @PostMapping("deletePaymentInfo/{id}")
    @Log(title = "删除 收款方式", businessType = BusinessType.DELETE, operatorType = OperatorType.MOBILE)
    public Response deletePaymentInfo(@PathVariable String id) {
        paymentInfoService.removeById(id);
        return Response.success();
    }

    @AuthToken
    @ApiImplicitParams({@ApiImplicitParam(name = "securityPassword", value = "安全密码", required = true), })
    @ApiOperation(value = "判断安全密码是否正确")
    @PostMapping("checkSecurityPassword")
    public Response checkSecurityPassword(@RequestBody HashMap<String, String> map) {
        String securityPassword = map.get("securityPassword");
        LambdaQueryWrapper<EzAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAdvertisingBusiness::getUserId, ContextHandler.getUserId());
        EzAdvertisingBusiness businessServiceOne = advertisingBusinessService.getOne(queryWrapper);
        if (!EncoderUtil.matches(securityPassword,businessServiceOne.getSecurityPassword())) {
            throw new SecurityPasswordNotMatchException();
        }
        return Response.success();
    }


    @ApiOperation(value = "发布广告订单限制")
    @GetMapping("orderLimit")
    public Response<OrderLimitRespDto> orderLimit() {
        OrderLimitRespDto orderLimitRespDto=new OrderLimitRespDto();
        orderLimitRespDto.setList(typeService.list());
        EzOtcConfig config = configService.getById(1L);
        orderLimitRespDto.setMaxPayTime(config.getMaxPayTime());
        orderLimitRespDto.setMinPayTime(config.getMinPayTime());
        return Response.success(orderLimitRespDto);
    }

   //    -----------------------------------------------------------------------------------------------------------
    @NoRepeatSubmit
    @ApiOperation(value = "发布广告订单")
    @PostMapping("releaseAdvertisingOrder")
    @AuthToken(kyc = true)
    @Log(title = "发布广告订单", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response releaseAdvertisingOrder(@RequestBody OtcOrderReqDto otcOrderReqDto) {
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


    @ApiOperation(value = "订单列表")
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
    @ApiOperation(value = "商户接单/拒接 (订单广告)")
    @PutMapping("operateOrderAd")
    @AuthToken
    @Log(title = "商户接单(订单广告)", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response operateOrderAd(@RequestBody OrderOperateReqDto orderOperateReqDto) {
        return otcOrderService.merchantOrder(orderOperateReqDto);
    }
    @NoRepeatSubmit
    @ApiOperation(value = "商户 下架广告订单")
    @PutMapping("offShelfOrder/{orderNo}")
    @AuthToken
    @Log(title = "商户 下架广告订单", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response offShelfOrder(@PathVariable String orderNo) {
        return otcOrderService.offShelfOrder(orderNo);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "用户 取消订单（两个状态可取消订单  1：接单广告（卖家未接受订单）用户免费取消 " +
            "2：接单广告/普通广告（用户未支付状态） 用户取消次数增加）")
    @PutMapping("cancelOrder/{matchOrderNo}")
    @AuthToken
    @Log(title = "用户 取消订单", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response cancelOrder(@PathVariable String matchOrderNo) {
        return orderMatchService.cancelOrder(matchOrderNo);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "买家确认付款")
    @PutMapping("confirmPayment/{matchOrderNo}")
    @AuthToken
    @Log(title = "买家确认付款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response confirmPayment(@PathVariable String matchOrderNo) {
        return orderMatchService.confirmPayment(matchOrderNo);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "卖家放款")
    @PutMapping("sellerPut/{matchOrderNo}")
    @AuthToken
    @Log(title = "卖家放款", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response sellerPut(@PathVariable String matchOrderNo) {
        return orderMatchService.sellerPut(matchOrderNo,false);
    }


    @ApiOperation(value = "一键卖币(只支持人民币)")
    @PostMapping("sellOneKey")
    @AuthToken(kyc = true)
    @Log(title = "一键卖币", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    public Response<PaymentDetails> sellOneKey(@RequestBody @Validated SellOneKeyReqDto sellOneKeyReqDto) {
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
    public Response appeal(@RequestBody AppealReqDto appealReqDto) {
       return appealService.appeal(appealReqDto);
    }

    @NoRepeatSubmit
    @ApiOperation(value = "取消申诉")
    @PutMapping("cancelAppeal/{orderMatchNo}")
    @AuthToken
    @Log(title = "取消申诉", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    public Response cancelAppeal(@PathVariable String orderMatchNo) {
        return appealService.cancelAppeal(orderMatchNo);
    }


    @ApiOperation(value = "根据订单号查询申诉详情")
    @AuthToken
    @GetMapping("appealInfo/{orderMatchNo}")
    public ResponseList<EzOtcOrderAppeal> appealInfo(@PathVariable String orderMatchNo){
        LambdaQueryWrapper<EzOtcOrderAppeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderAppeal::getOrderMatchNo,orderMatchNo);
        return ResponseList.success(appealService.list(queryWrapper));
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
    public Response paymentInfo(@PathVariable String orderMatchNo, @PathVariable String orderPaymentId) {
        EzOtcOrderMatch orderMatch = orderMatchService.getById(orderMatchNo);
        orderMatch.setOrderPaymentId(orderPaymentId);
        orderMatchService.updateById(orderMatch);
        return Response.success();
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
            queryWrapper.eq(EzOtcOrder::getUserId, ContextHandler.getUserId()).and(wq->wq.eq(EzOtcOrder::getStatus, 0));
        } else {
            queryWrapper.eq(EzOtcOrder::getUserId, userId).and(wq->wq.eq(EzOtcOrder::getStatus, 0));
        }
        List<EzOtcOrder> list = otcOrderService.list(queryWrapper);
        List<ReleaseOrderRespDto> releaseOrderRespDtos = new ArrayList<>();

        list.forEach(e -> {
            List<Integer> paymentMethods = new ArrayList<>();
            ReleaseOrderRespDto releaseOrderRespDto = new ReleaseOrderRespDto();
            if (StringUtils.isNotNull(e.getPaymentMethod1())) {
                paymentMethods.add(e.getPaymentMethod1());
            }
            if (StringUtils.isNotNull(e.getPaymentMethod2())) {
                paymentMethods.add(e.getPaymentMethod2());
            }
            if (StringUtils.isNotNull(e.getPaymentMethod3())) {
                paymentMethods.add(e.getPaymentMethod3());
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
    public Response sendChat(@RequestBody EzOtcChatMsg ezOtcChatMsg) {
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
