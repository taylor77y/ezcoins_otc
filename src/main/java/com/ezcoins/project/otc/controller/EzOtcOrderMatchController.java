package com.ezcoins.project.otc.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.RecordSonType;
import com.ezcoins.constant.SysTipsConstants;
import com.ezcoins.constant.SystemOrderTips;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.manager.AsyncManager;
import com.ezcoins.manager.factory.AsyncFactory;
import com.ezcoins.project.coin.entity.Record;
import com.ezcoins.project.coin.entity.resp.AccountRespDto;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.coin.service.RecordService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.*;
import com.ezcoins.project.otc.entity.query.OneSellQuery;
import com.ezcoins.project.otc.entity.resp.OneOrderRespDto;
import com.ezcoins.project.otc.service.EzOtcChatMsgService;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.project.otc.service.EzOtcOrderPaymentService;
import com.ezcoins.project.otc.service.EzSellConfigService;
import com.ezcoins.response.Response;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import com.ezcoins.websocket.WebSocketHandle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 匹配日OTC订单 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-06-18
 */
@RestController
@Api(tags = "Admin-OTC匹配订单模块")
@RequestMapping("/admin/otc/ezOtcOrderMatch")
public class EzOtcOrderMatchController {

    @Autowired
    private EzOtcOrderMatchService orderMatchService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EzOtcOrderPaymentService paymentService;

    @Autowired
    private EzUserKycService kycService;

    @Autowired
    private EzUserService userService;

    @Autowired
    private RecordService recordService;

    @AuthToken
    @ApiOperation(value = "OTC 匹配订单列表")
    @PostMapping("otcOrderList")
    public ResponsePageList<EzOtcOrderMatch> otcOrderList(@RequestBody SearchModel<EzOtcOrderMatch> searchModel) {
        return ResponsePageList.success(orderMatchService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }


    @AuthToken
    @ApiOperation(value = "一键卖币订单")
    @PostMapping("oneSellOrderList")
    public ResponsePageList<OneOrderRespDto> oneSellOrderList(@RequestBody OneSellQuery oneSellQuery) {
        Page<EzOtcOrderMatch> page = new Page<>(oneSellQuery.getPage(), oneSellQuery.getLimit());
        LambdaQueryWrapper<EzOtcOrderMatch> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(oneSellQuery.getCoinName())) {
            queryWrapper.eq(EzOtcOrderMatch::getCoinName, oneSellQuery.getCoinName());
        }
        if (StringUtils.isNotEmpty(oneSellQuery.getOrderMatchNo())) {
            queryWrapper.eq(EzOtcOrderMatch::getOrderMatchNo, oneSellQuery.getOrderMatchNo());
        }
        if (StringUtils.isNotEmpty(oneSellQuery.getMatchAdvertisingName())) {
            queryWrapper.like(EzOtcOrderMatch::getMatchAdvertisingName, oneSellQuery.getMatchAdvertisingName());
        }
        if (StringUtils.isNotEmpty(oneSellQuery.getUserId())) {
            queryWrapper.like(EzOtcOrderMatch::getUserId, oneSellQuery.getUserId());
        }
        if (StringUtils.isNotNull(oneSellQuery.getStatuss()) && oneSellQuery.getStatuss().size() > 0) {
            queryWrapper.in(EzOtcOrderMatch::getStatus, oneSellQuery.getStatuss());
        }
        queryWrapper.orderByDesc(EzOtcOrderMatch::getCreateTime);
        queryWrapper.eq(EzOtcOrderMatch::getOrderType, "2");
        Page<EzOtcOrderMatch> matchPage = orderMatchService.page(page, queryWrapper);

        List<OneOrderRespDto> respDtos = BeanUtils.copyListProperties(matchPage.getRecords(), OneOrderRespDto::new);

        LambdaQueryWrapper<EzOtcOrderPayment> paymentMethodLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<String> collect1 = respDtos.stream().map(OneOrderRespDto::getOrderPaymentId).collect(Collectors.toList());
        if (collect1.size()>0){
            paymentMethodLambdaQueryWrapper.in(EzOtcOrderPayment::getId, collect1);
        }
        Map<String, EzOtcOrderPayment> collect = paymentService.list(paymentMethodLambdaQueryWrapper).stream().collect(Collectors.toMap(EzOtcOrderPayment::getId, Function.identity()));

        LambdaQueryWrapper<EzUserKyc> kycLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<String> collect2 = respDtos.stream().map(OneOrderRespDto::getUserId).collect(Collectors.toList());
        if (collect2.size()>0){
            kycLambdaQueryWrapper.in(EzUserKyc::getUserId,collect2);
        }
        Map<String, EzUserKyc> kycMap = kycService.list(kycLambdaQueryWrapper)
                .stream().collect(Collectors.toMap(EzUserKyc::getUserId, Function.identity()));

        respDtos.forEach(e -> {
            e.setPayment(collect.get(e.getOrderPaymentId()));
            EzUserKyc ezUserKyc = kycMap.get(e.getUserId());
            e.setPhoneNum("+"+ezUserKyc.getCountryTelCode()+"-"+ezUserKyc.getContactNumber());
        });

        Page<OneOrderRespDto> tPage = new Page<>();
        tPage.setRecords(respDtos);
        tPage.setTotal(matchPage.getTotal());
        return ResponsePageList.success(tPage);
    }


    @AuthToken
    @NoRepeatSubmit
    @ApiOperation(value = "后台一键卖币确认付款")
    @PostMapping("confirmPayment/{matchOrderNo}/{status}")
    @Log(title = "OTC模块", logInfo = "后台一键卖币确认付款", operatorType = OperatorType.MANAGE)
    public Response confirmPayment(@PathVariable String matchOrderNo, @PathVariable String status) {
        //根据订单号查询到订单
        EzOtcOrderMatch orderMatch = orderMatchService.getById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            return Response.error("订单状态已发生变化");
        }
        BigDecimal amount = orderMatch.getAmount();
        BigDecimal totalAmount = amount.add(orderMatch.getFee());

        String ss = null;
        SysTipsConstants.TipsType tip = null;
        BigDecimal num = null;

        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b1 = new BalanceChange();
        b1.setCoinName(orderMatch.getCoinName());
        b1.setUserId(orderMatch.getUserId());

        if ("0".equals(status)) { //确认付款   自己放款
            //手续费计算
            b1.setFrozen(totalAmount.negate());
            b1.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            b1.setMainType(CoinConstants.MainType.NORECORD.getType());
            cList.add(b1);

            Record rec = new Record();
            rec.setUserId(orderMatch.getUserId());
            rec.setCoinName(orderMatch.getCoinName());
            rec.setFee(BigDecimal.ZERO);
            rec.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            rec.setMainType(CoinConstants.MainType.TRANSFEROUT.getType());
            rec.setSonType(RecordSonType.TRANSFER_OUT);
            rec.setStatus(CoinConstants.RecordStatus.OK.getStatus());
            rec.setAmount(totalAmount.negate());
            rec.setCreateBy(userService.getById(orderMatch.getUserId()).getUserName());
            recordService.save(rec);

            WebSocketHandle.accountChange(orderMatch.getUserId(), orderMatch.getCoinName(), amount, RecordSonType.TRANSFER_OUT);
            Date nowDate = DateUtils.getNowDate();
            orderMatch.setPaymentTime(nowDate);
            orderMatch.setFinishTime(nowDate);
            //给用户一个信号
            ss = MatchOrderStatus.COMPLETED.getCode();
            tip = SysTipsConstants.TipsType.SELL_SUCCESS;
            num = amount;
        } else {
            //解冻用户卖出 数量
            b1.setAvailable(totalAmount);
            b1.setFrozen(totalAmount.negate());
            b1.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
            b1.setMainType(CoinConstants.MainType.UNFREEZE.getType());
            b1.setFee(BigDecimal.ZERO);
            b1.setSonType(RecordSonType.TRANSACTION_UNFREEZE);
            cList.add(b1);
            ss = MatchOrderStatus.CANCELLED.getCode();
            tip = SysTipsConstants.TipsType.SELL_FAIL;
            num = totalAmount;
        }
        AsyncManager.me().execute(AsyncFactory.StationLetter(orderMatch.getUserId(),
                tip, orderMatch.getOrderMatchNo(), num.toString() +
                        orderMatch.getCoinName(),
                        orderMatch.getTotalPrice()
                        .setScale(2,RoundingMode.FLOOR)
                        +orderMatch.getCurrencyCode()));
        //给用户一个信号
        WebSocketHandle.orderStatusChange(orderMatch.getUserId(), ss);
        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        orderMatch.setStatus(ss);
        orderMatchService.updateById(orderMatch);
        return Response.success();
    }


    @Autowired
    private EzOtcOrderPaymentService orderPaymentService;


    @AuthToken
    @ApiOperation(value = "根据订单号查询支付方式(一键卖币)")
    @PostMapping("paymentInfo/{matchOrderNo}")
    public Response<EzOtcOrderPayment> paymentInfo(@PathVariable String matchOrderNo) {
        LambdaQueryWrapper<EzOtcOrderPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzOtcOrderPayment::getOrderMatchNo, matchOrderNo);
        return Response.success(orderPaymentService.getOne(lambdaQueryWrapper));
    }

}

