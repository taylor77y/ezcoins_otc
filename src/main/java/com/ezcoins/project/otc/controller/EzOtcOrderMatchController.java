package com.ezcoins.project.otc.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.coin.service.RecordService;
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
import com.ezcoins.websocket.WebSocketHandle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
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
    private AccountService accountService;

    @Autowired
    private RecordService recordService;

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
        BigDecimal amount =orderMatch.getAmount();
        BigDecimal totalAmount = amount.add(orderMatch.getFee());

        String ss=null;
        SysTipsConstants.TipsType tip=null;
        BigDecimal num=null;

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
            recordService.save(rec);
            WebSocketHandle.accountChange(orderMatch.getUserId(), orderMatch.getCoinName(), amount, RecordSonType.TRANSFER_OUT);

            Date nowDate = DateUtils.getNowDate();
            orderMatch.setPaymentTime(nowDate);
            orderMatch.setFinishTime(nowDate);
            //给用户一个信号
            ss=MatchOrderStatus.COMPLETED.getCode();
            tip=SysTipsConstants.TipsType.SELL_SUCCESS;
            num=amount;
        } else {
            //解冻用户卖出 数量
            b1.setAvailable(totalAmount);
            b1.setFrozen(totalAmount.negate());
            b1.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
            b1.setMainType(CoinConstants.MainType.UNFREEZE.getType());
            b1.setFee(BigDecimal.ZERO);
            b1.setSonType(RecordSonType.TRANSACTION_UNFREEZE);
            cList.add(b1);
            ss=MatchOrderStatus.CANCELLED.getCode();
            tip=SysTipsConstants.TipsType.SELL_FAIL;
            num=totalAmount;
        }
        AsyncManager.me().execute(AsyncFactory.StationLetter(orderMatch.getUserId(),
                tip, orderMatch.getOrderMatchNo(),num.toString()+orderMatch.getCoinName()));
        //给用户一个信号
        WebSocketHandle.orderStatusChange(orderMatch.getUserId(),ss);
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

