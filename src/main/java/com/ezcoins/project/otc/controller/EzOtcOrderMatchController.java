package com.ezcoins.project.otc.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.constant.RecordSonType;
import com.ezcoins.constant.SystemOrderTips;
import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.otc.entity.*;
import com.ezcoins.project.otc.service.EzOtcChatMsgService;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.project.otc.service.EzOtcOrderPaymentService;
import com.ezcoins.project.otc.service.EzSellConfigService;
import com.ezcoins.response.Response;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.MessageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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
    private EzOtcChatMsgService otcChatMsgService;

    @Autowired
    private EzSellConfigService sellConfigService;

    @Autowired
    private AccountService accountService;

    @AuthToken
    @ApiOperation(value = "OTC 匹配订单列表")
    @PostMapping("otcOrderList")
    public ResponsePageList<EzOtcOrderMatch> otcOrderList(@RequestBody SearchModel<EzOtcOrderMatch> searchModel) {
        return ResponsePageList.success(orderMatchService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @AuthToken
    @NoRepeatSubmit
    @ApiOperation(value = "后台一键卖币确认付款")
    @PostMapping("confirmPayment/{matchOrderNo}/{status}")
    @Log(title = "OTC模块", logInfo ="后台一键卖币确认付款", operatorType = OperatorType.MANAGE)
    public Response confirmPayment(@PathVariable String matchOrderNo, @PathVariable String status) {
        //根据订单号查询到订单
        EzOtcOrderMatch orderMatch = orderMatchService.getById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            return Response.error("订单状态已发生变化");
        }
        List<EzOtcChatMsg> list = new ArrayList<>();
        EzOtcChatMsg ezOtcChatMsg1 = new EzOtcChatMsg();
        ezOtcChatMsg1.setOrderMatchNo(orderMatch.getOrderMatchNo());
        ezOtcChatMsg1.setReceiveUserId(orderMatch.getUserId());
        String n = null;

        if ("0".equals(status)) { //确认付款
            orderMatch.setPaymentTime(DateUtils.getNowDate());
            n = MatchOrderStatus.PAID.getCode();
            orderMatch.setStatus(n);
            ezOtcChatMsg1.setSendText(SystemOrderTips.PAYMENT_SELL);
        } else {
            n = MatchOrderStatus.CANCELLED.getCode();
            ezOtcChatMsg1.setSendText(n);
            orderMatch.setStatus(MatchOrderStatus.CANCELLED.getCode());
            //冻结用户卖出 数量
            List<BalanceChange> cList = new ArrayList<>();
            LambdaQueryWrapper<EzOneSellConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EzOneSellConfig::getCoinName, orderMatch.getCoinName());
            EzOneSellConfig ezSellConfig = sellConfigService.getOne(queryWrapper);
            BigDecimal amount = orderMatch.getAmount();
            //手续费计算
            BigDecimal fee = amount.multiply(ezSellConfig.getFeeRatio()).setScale(8, RoundingMode.FLOOR).add(ezSellConfig.getFee());
            BigDecimal add = orderMatch.getAmount().add(fee);
            BalanceChange b1 = new BalanceChange();
            b1.setAvailable(add);
            b1.setCoinName(orderMatch.getCoinName());
            b1.setFrozen(add.negate());
            b1.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            b1.setMainType(CoinConstants.MainType.UNFREEZE.getType());
            b1.setFee(BigDecimal.ZERO);
            b1.setSonType(RecordSonType.TRANSACTION_UNFREEZE);
            b1.setUserId(orderMatch.getUserId());
            cList.add(b1);
            if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
               throw new AccountOperationBusyException();
            }
        }
        list.add(ezOtcChatMsg1);
        orderMatchService.updateById(orderMatch);
        otcChatMsgService.sendSysChat(list, n);
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

