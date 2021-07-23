package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.RecordSonType;
import com.ezcoins.constant.SystemOrderTips;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.constant.interf.IndexOrderNoKey;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.project.coin.entity.Record;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.coin.service.RecordService;
import com.ezcoins.project.common.mq.service.ConvertAndSendService;
import com.ezcoins.project.otc.entity.*;
import com.ezcoins.project.otc.entity.req.AdMatchOrderQueryReqDto;
import com.ezcoins.project.otc.entity.req.OrderRecordQueryReqDto;
import com.ezcoins.project.otc.entity.req.SellOneKeyReqDto;
import com.ezcoins.project.otc.entity.resp.OrderRecordRespDto;
import com.ezcoins.project.otc.entity.resp.OtcInfoOrder;
import com.ezcoins.project.otc.entity.resp.PaymentDetails;
import com.ezcoins.project.otc.mapper.EzOtcOrderMatchMapper;
import com.ezcoins.project.otc.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import com.ezcoins.websocket.WebSocketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 匹配日OTC订单 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-18
 */
@Service
public class EzOtcOrderMatchServiceImpl extends ServiceImpl<EzOtcOrderMatchMapper, EzOtcOrderMatch> implements EzOtcOrderMatchService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private EzOtcOrderService otcOrderService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EzSellConfigService sellConfigService;

    @Autowired
    private EzOtcOrderIndexService orderIndexService;

    @Autowired
    private EzAdvertisingBusinessService businessService;

    @Autowired
    private EzOtcOrderPaymentService orderPaymentService;

    @Autowired
    private RecordService recordService;


    /***
     * @Description: 用户 取消订单（两个状态可取消订单  1：接单广告（卖家未接受订单）用户免费取消 2：接单广告/普通广告（用户未支付状态） 用户取消次数增加）
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/18
     * @param matchOrderNo
     */
    @Override
    public Response cancelOrder(String matchOrderNo) {
        String userId = ContextHandler.getUserId();

        //根据订单号查询到订单
        EzOtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        String orderNo = orderMatch.getOrderNo();
        String orderMatchNo = orderMatch.getOrderMatchNo();


        //查询到上架订单
        EzOtcOrder otcOrder = otcOrderService.getById(orderNo);
        List<EzOtcChatMsg> list = new ArrayList<>();
        EzOtcChatMsg ezOtcChatMsg = new EzOtcChatMsg();
        EzOtcChatMsg ezOtcChatMsg1 = new EzOtcChatMsg();
        ezOtcChatMsg.setOrderMatchNo(orderMatchNo);
        ezOtcChatMsg1.setOrderMatchNo(orderMatchNo);

        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        b.setCoinName(orderMatch.getCoinName());
        b.setAvailable(orderMatch.getAmount());
        b.setFrozen(orderMatch.getAmount().negate());
        b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
        b.setMainType(CoinConstants.MainType.UNFREEZE.getType());
        b.setSonType(RecordSonType.TRANSACTION_UNFREEZE);
        //查看订单状态
        if (orderMatch.getStatus().equals(MatchOrderStatus.PENDINGORDER.getCode())) {
            //用户免费取消
            ezOtcChatMsg.setSendText(SystemOrderTips.CANCEL_2);
            ezOtcChatMsg1.setSendText(SystemOrderTips.ORDERS_CANCEL);
            ezOtcChatMsg.setReceiveUserId(orderMatch.getOtcOrderUserId());
            ezOtcChatMsg1.setReceiveUserId(orderMatch.getUserId());
            list.add(ezOtcChatMsg);
            list.add(ezOtcChatMsg1);
            otcChatMsgService.sendSysChat(list, MatchOrderStatus.ORDERBEENCANCELLED.getCode());
            orderMatch.setStatus(MatchOrderStatus.ORDERBEENCANCELLED.getCode());
        } else if (orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            orderMatch.setStatus(MatchOrderStatus.CANCELLED.getCode());
            //将订单匹配数量增加回去
            otcOrder.setQuotaAmount(otcOrder.getQuotaAmount().subtract(orderMatch.getAmount()));
            //判断是谁取消的订单
            if (otcOrder.getUserId().equals(userId)) {//商户取消的订单
                if ("1".equals(otcOrder.getType())) {//卖单
                    //退后用户冻结的订单
                }
                if ("0".equals(otcOrder.getType())) {//买单
                    //解冻金额
                    b.setUserId(orderMatch.getUserId());
                    ezOtcChatMsg.setSendText(SystemOrderTips.CANCEL_2);
                    ezOtcChatMsg.setReceiveUserId(orderMatch.getUserId());
                    ezOtcChatMsg1.setReceiveUserId(userId);
                    ezOtcChatMsg1.setSendText(SystemOrderTips.CANCEL);
                    list.add(ezOtcChatMsg);
                    list.add(ezOtcChatMsg1);
                    cList.add(b);
                    if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                        throw new AccountOperationBusyException();
                    }
                }
            } else {//用户取消的订单
                //判断订单类型

                System.out.println(otcOrder.getType());
                if ("1".equals(otcOrder.getType())) {//卖单
                    //退后用户冻结的订单
                }
                if ("0".equals(otcOrder.getType())) {//买单
                    //解冻金额
                    b.setUserId(userId);
                    ezOtcChatMsg.setSendText(SystemOrderTips.CANCEL_2);
                    ezOtcChatMsg.setReceiveUserId(otcOrder.getUserId());
                    ezOtcChatMsg1.setReceiveUserId(userId);
                    ezOtcChatMsg1.setSendText(SystemOrderTips.CANCEL);
                    list.add(ezOtcChatMsg);
                    list.add(ezOtcChatMsg1);
                    cList.add(b);
                    if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                        throw new AccountOperationBusyException();
                    }
                }
                //查询当前用户取消订单数量
                int count = 1;
                Object object = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId);
                if (null != object) {
                    count = (Integer) object;
                    count += 1; //用户取消次数增加
                }
                redisCache.setCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId, count, Math.toIntExact(DateUtils.getSecondsNextEarlyMorning()), TimeUnit.SECONDS);
            }
        } else {
            throw new BaseException("订单状态已发生变化");
        }
        otcOrderService.updateById(otcOrder);
        otcChatMsgService.sendSysChat(list, MatchOrderStatus.CANCELLED.getCode());
        baseMapper.updateById(orderMatch);
        return Response.success();
    }

    @Autowired
    private EzOtcChatMsgService otcChatMsgService;

    /**
     * @param matchOrderNo
     * @Description: 买家确认 付款
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/19
     */
    @Override
    public Response confirmPayment(String matchOrderNo) {
        //根据订单号查询到订单
        EzOtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            return Response.error(MessageUtils.message("订单状态已发生变化"));
        }
        orderMatch.setPaymentTime(DateUtils.getNowDate());
        orderMatch.setStatus(MatchOrderStatus.PAID.getCode());

        //TODO:存入消息
        EzOtcChatMsg ezOtcChatMsg1 = new EzOtcChatMsg();
        EzOtcChatMsg ezOtcChatMsg2 = new EzOtcChatMsg();
        List<EzOtcChatMsg> list = new ArrayList<>();
        ezOtcChatMsg1.setOrderMatchNo(orderMatch.getOrderMatchNo());
        ezOtcChatMsg2.setOrderMatchNo(orderMatch.getOrderMatchNo());
        String userId = ContextHandler.getUserId();
        if ("0".equals(orderMatch.getType())) {//买单
            if (userId.equals(orderMatch.getUserId())) {
                throw new BaseException(MessageUtils.message("订单错误"));
            }
            ezOtcChatMsg1.setSendText(SystemOrderTips.PAYMENT_SELL);
            ezOtcChatMsg1.setReceiveUserId(orderMatch.getUserId());
            list.add(ezOtcChatMsg1);
            ezOtcChatMsg2.setSendText(SystemOrderTips.PAYMENT_BUY);
            ezOtcChatMsg2.setReceiveUserId(userId);
            list.add(ezOtcChatMsg2);
        } else {
            if (!userId.equals(orderMatch.getUserId())) {
                throw new BaseException(MessageUtils.message("订单错误"));
            }
            ezOtcChatMsg1.setSendText(SystemOrderTips.PAYMENT_BUY);
            ezOtcChatMsg1.setReceiveUserId(userId);
            list.add(ezOtcChatMsg1);

            ezOtcChatMsg2.setSendText(SystemOrderTips.PAYMENT_SELL);
            ezOtcChatMsg2.setReceiveUserId(orderMatch.getOtcOrderUserId());
            list.add(ezOtcChatMsg2);
        }
        otcChatMsgService.sendSysChat(list, MatchOrderStatus.PAID.getCode());
        baseMapper.updateById(orderMatch);
        return Response.success();
    }


    /**
     * @param matchOrderNo
     * @Description: 卖家放款
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/19
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response sellerPut(String matchOrderNo, boolean isAdmin) {
        String userId = ContextHandler.getUserId();
        Date nowDate = DateUtils.getNowDate();
        EzOtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.PAID.getCode())) {
            throw new BaseException(MessageUtils.message("订单状态已发生变化"));
        }
//        if ("0".equals(orderMatch.getIsAppeal()) && !isAdmin) {
//            return Response.error(MessageUtils.message("订单已被申诉，不能放款"));
//        }
        List<BalanceChange> cList = new ArrayList<>();
        //匹配订单分为 买单 和 卖单  //查询到otc 订单
        BigDecimal amount = orderMatch.getAmount();


        if (orderMatch.getOrderNo() == null) {//一键卖币放行
            BalanceChange b1 = new BalanceChange();
            b1.setCoinName(orderMatch.getCoinName());
            LambdaQueryWrapper<EzOneSellConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EzOneSellConfig::getCoinName, orderMatch.getCoinName());
            EzOneSellConfig ezSellConfig = sellConfigService.getOne(queryWrapper);

            //手续费计算
            BigDecimal fee = amount.multiply(ezSellConfig.getFeeRatio()).setScale(8, RoundingMode.FLOOR).add(ezSellConfig.getFee());
            BigDecimal add = orderMatch.getAmount().add(fee);
            b1.setFrozen(add.negate());
            b1.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            b1.setMainType(CoinConstants.MainType.NORECORD.getType());
            b1.setUserId(userId);
            cList.add(b1);
            if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                throw new AccountOperationBusyException();
            }
            Record rec = new Record();
            rec.setUserId(userId);
            rec.setCoinName(orderMatch.getCoinName());
            rec.setFee(BigDecimal.ZERO);
            rec.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            rec.setMainType(CoinConstants.MainType.TRANSFEROUT.getType());
            rec.setSonType(RecordSonType.TRANSFER_OUT);
            rec.setStatus(CoinConstants.RecordStatus.OK.getStatus());
            rec.setAmount(amount.negate());
            recordService.save(rec);
            WebSocketHandle.accountChange(userId, orderMatch.getCoinName(), amount, RecordSonType.TRANSFER_OUT);

            orderMatch.setStatus(MatchOrderStatus.COMPLETED.getCode());
            orderMatch.setFinishTime(nowDate);
            baseMapper.updateById(orderMatch);
            //给用户一个信号
            WebSocketHandle.orderStatusChange(userId, MatchOrderStatus.COMPLETED.getCode());
            return Response.success();
        }



        EzOtcOrder ezOtcOrder = otcOrderService.getById(orderMatch.getOrderNo());
        //减少冻结的币
        BigDecimal frozeAmount = ezOtcOrder.getFrozeAmount();
        BalanceChange b1 = new BalanceChange();
        b1.setCoinName(orderMatch.getCoinName());
        b1.setFrozen(amount.negate());
        b1.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b1.setMainType(CoinConstants.MainType.NORECORD.getType());
        b1.setFee(BigDecimal.ZERO);
        b1.setSonType(RecordSonType.TRANSFER_OUT);

        BalanceChange b2 = new BalanceChange();
        b2.setCoinName(orderMatch.getCoinName());
        b2.setAvailable(amount);
        b2.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
        b2.setMainType(CoinConstants.MainType.TRANSFERIN.getType());
        b2.setFee(BigDecimal.ZERO);
        b2.setSonType(RecordSonType.TRANSFER_IN);

        //改变OTC信息
        String sellUserId = null;
        String buyUserId = null;
        Date payTime = orderMatch.getPaymentTime();
        List<EzOtcChatMsg> list = new ArrayList<>();
        EzOtcChatMsg ezOtcChatMsg1 = new EzOtcChatMsg();
        EzOtcChatMsg ezOtcChatMsg2 = new EzOtcChatMsg();
        ezOtcChatMsg1.setOrderMatchNo(orderMatch.getOrderMatchNo());
        ezOtcChatMsg2.setOrderMatchNo(orderMatch.getOrderMatchNo());
        if ("0".equals(ezOtcOrder.getType())) {//买单
            if (isAdmin) {
                userId = orderMatch.getUserId();
            }
            if (userId.equals(ezOtcOrder.getUserId())) {
                throw new BaseException("订单错误");
            }
            b1.setUserId(userId);
            cList.add(b1);

            Record rec = new Record();
            rec.setUserId(userId);
            rec.setCoinName(ezOtcOrder.getCoinName());
            rec.setFee(BigDecimal.ZERO);
            rec.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            rec.setMainType(CoinConstants.MainType.TRANSFEROUT.getType());
            rec.setSonType(RecordSonType.TRANSFER_OUT);
            rec.setStatus(CoinConstants.RecordStatus.OK.getStatus());
            rec.setAmount(amount.negate());
            recordService.save(rec);

            ezOtcChatMsg1.setSendText(SystemOrderTips.RELEASE_2);
            ezOtcChatMsg1.setReceiveUserId(userId);
            WebSocketHandle.accountChange(rec.getUserId(),rec.getCoinName(),rec.getAmount(),rec.getSonType());


            //将amount币存入买家的账户中
            b2.setUserId(ezOtcOrder.getUserId());
            cList.add(b2);
            ezOtcChatMsg2.setSendText(SystemOrderTips.RELEASE);
            ezOtcChatMsg2.setReceiveUserId(ezOtcOrder.getUserId());
            list.add(ezOtcChatMsg1);
            list.add(ezOtcChatMsg2);
            sellUserId = userId;
            buyUserId = ezOtcOrder.getUserId();

        } else if ("1".equals(ezOtcOrder.getType())) {  //卖单 需要广告商户进行放币
            if (isAdmin) {
                userId = ezOtcOrder.getUserId();
            }
            if (!userId.equals(ezOtcOrder.getUserId())) {
                throw new BaseException("订单错误");
            }
            b1.setUserId(userId);//发布订单用户id
            cList.add(b1);

            ezOtcChatMsg1.setSendText(SystemOrderTips.RELEASE);
            ezOtcChatMsg1.setReceiveUserId(userId);
            Record rec = new Record();
            rec.setUserId(userId);
            rec.setCoinName(ezOtcOrder.getCoinName());
            rec.setFee(BigDecimal.ZERO);
            rec.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            rec.setMainType(CoinConstants.MainType.TRANSFEROUT.getType());
            rec.setSonType(RecordSonType.TRANSFER_OUT);
            rec.setStatus(CoinConstants.RecordStatus.OK.getStatus());
            rec.setAmount(amount.negate());
            recordService.save(rec);
            WebSocketHandle.accountChange(rec.getUserId(),rec.getCoinName(),rec.getAmount(),rec.getSonType());


            //将amount币存入买家的账户中
            b2.setUserId(orderMatch.getUserId());
            cList.add(b2);
            ezOtcChatMsg2.setSendText(SystemOrderTips.RELEASE_2);
            ezOtcChatMsg2.setReceiveUserId(userId);

            list.add(ezOtcChatMsg1);
            list.add(ezOtcChatMsg2);

            sellUserId = userId;
            buyUserId = orderMatch.getUserId();

            //otc订单冻结币的剩余
            BigDecimal frozeNow = frozeAmount.subtract(amount);
            ezOtcOrder.setFrozeAmount(frozeNow);
            otcOrderService.updateById(ezOtcOrder);
        }
        if (isAdmin) {
            ezOtcChatMsg1.setSendText(SystemOrderTips.APPEAL_PUT);
            ezOtcChatMsg2.setSendText(SystemOrderTips.APPEAL_PUT);
            list.add(ezOtcChatMsg1);
            list.add(ezOtcChatMsg2);
        }

        otcChatMsgService.sendSysChat(list, MatchOrderStatus.COMPLETED.getCode());
        //改变订单状态
        orderMatch.setStatus(MatchOrderStatus.COMPLETED.getCode());
        orderMatch.setFinishTime(nowDate);
        baseMapper.updateById(orderMatch);
        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        businessService.updateCount(sellUserId, buyUserId, payTime, nowDate, isAdmin, "0");
        return Response.success();
    }

    /**
     * 付款失败  后台修改订单为取消
     *
     * @param matchOrderNo
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void paymentFail(String matchOrderNo) {
        EzOtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.PAID.getCode())) {
            throw new BaseException("订单状态已发生变化");
        }
        //匹配订单分为 买单 和 卖单  //查询到otc 订单
        EzOtcOrder ezOtcOrder = otcOrderService.getById(orderMatch.getOrderNo());
        ezOtcOrder.setQuotaAmount(ezOtcOrder.getQuotaAmount().subtract(orderMatch.getAmount()));
        otcOrderService.updateById(ezOtcOrder);
        List<EzOtcChatMsg> list = new ArrayList<>();
        EzOtcChatMsg ezOtcChatMsg = new EzOtcChatMsg();
        EzOtcChatMsg ezOtcChatMsg1 = new EzOtcChatMsg();
        ezOtcChatMsg1.setOrderMatchNo(orderMatch.getOrderMatchNo());
        ezOtcChatMsg.setOrderMatchNo(orderMatch.getOrderMatchNo());
        ezOtcChatMsg.setReceiveUserId(orderMatch.getOtcOrderUserId());
        ezOtcChatMsg1.setReceiveUserId(orderMatch.getUserId());
        ezOtcChatMsg.setSendText(SystemOrderTips.APPEAL_CANCEL);
        ezOtcChatMsg1.setSendText(SystemOrderTips.APPEAL_CANCEL);

        orderMatch.setStatus(MatchOrderStatus.CANCELLED.getCode());
        baseMapper.updateById(orderMatch);
        list.add(ezOtcChatMsg);
        list.add(ezOtcChatMsg1);
        otcChatMsgService.sendSysChat(list, MatchOrderStatus.CANCELLED.getCode());
        String buyUserId;
        String sellUserId;
        //交易类型(0:买  1：卖)
        if ("0".equals(orderMatch.getType())){
            buyUserId=orderMatch.getOtcOrderUserId();
            sellUserId=orderMatch.getUserId();
        }else {
            buyUserId=orderMatch.getUserId();
            sellUserId=orderMatch.getOtcOrderUserId();
        }
        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        b.setCoinName(orderMatch.getCoinName());
        b.setAvailable(orderMatch.getAmount());
        b.setFrozen(orderMatch.getAmount().negate());
        b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
        b.setMainType(CoinConstants.MainType.FROZEN.getType());
        b.setUserId(sellUserId);
        cList.add(b);
        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        //降低买单完成率
        businessService.updateCount(sellUserId,buyUserId,null,null,true,"1");
    }


    /**
     * @param userId
     * @Description:
     * @Param: [userId]
     * @return: com.ezcoins.response.Response<com.ezcoins.project.otc.entity.resp.OtcInfoOrder>
     * @Author: Wanglei
     * @Date: 2021/6/25
     */
    @Override
    public Response<OtcInfoOrder> otcOrderListBy(String userId) {
        return null;
    }


    @Autowired
    private EzPaymentInfoService paymentInfoService;

    @Autowired
    private EzOtcOrderPaymentService paymentService;

    @Autowired
    private EzOtcConfigService otcConfigService;

    @Autowired
    private ConvertAndSendService convertAndSend;

    /**
     * 一键卖币 (只支持人民币)
     *
     * @param sellOneKeyReqDto
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response<PaymentDetails> sellOneKey(SellOneKeyReqDto sellOneKeyReqDto) {
        String userId = ContextHandler.getUserId();
        //获取otc基本配置
        EzOtcConfig otcConfig = otcConfigService.getById(1);
        //查询当前用户是否被取消订单是否超过规定数量
        Integer maxCancelNum = otcConfig.getMaxCancelNum();
        Object object = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId);
        if (object != null && (Integer) object > maxCancelNum) {//5后面从配置数据库得到
            return Response.error(MessageUtils.message("你今天取消次数超过上线,每天再来"));
        }
        PaymentDetails details = new PaymentDetails();
        //查看用户是否有未完成的订单
        LambdaQueryWrapper<EzOtcOrderMatch> matchLambdaQueryWrapper = Wrappers.<EzOtcOrderMatch>lambdaQuery().
                eq(EzOtcOrderMatch::getUserId, userId).eq(EzOtcOrderMatch::getType,"0")
                .and(wq -> wq.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode())
                .or().eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode())
                .or().eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PAID.getCode()));
        EzOtcOrderMatch orderMatch = baseMapper.selectOne(matchLambdaQueryWrapper);//匹配订单是否有未完成

        if (orderMatch != null) {
            BeanUtils.copyBeanProp(details, orderMatch);
            LambdaQueryWrapper<EzOtcOrderPayment> paymentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            paymentLambdaQueryWrapper.eq(EzOtcOrderPayment::getOrderMatchNo, orderMatch.getOrderMatchNo());
            details.setEzOtcOrderPayments(paymentService.list(paymentLambdaQueryWrapper));
            return Response.success(MessageUtils.message("请先完成当前未完成的订单"), details);//将订单返回
        }
        LambdaQueryWrapper<EzOneSellConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOneSellConfig::getCoinName, sellOneKeyReqDto.getCoinName());
        EzOneSellConfig ezSellConfig = sellConfigService.getOne(queryWrapper);
        if ("1".equals(ezSellConfig.getStatus())) {
            return Response.error(MessageUtils.message("当前币种一键卖币已关闭"));
        }
        BigDecimal amount = sellOneKeyReqDto.getAmount();
        BigDecimal maxAmount = ezSellConfig.getMaxAmount();
        BigDecimal minAmount = ezSellConfig.getMinAmount();
        if (amount.compareTo(maxAmount) > 0 || amount.compareTo(minAmount) < 0) {
            return Response.error(MessageUtils.message("输入数量不满足条件范围"));
        }

        LambdaQueryWrapper<EzPaymentInfo> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(EzPaymentInfo::getUserId, userId);
        queryWrapper1.eq(EzPaymentInfo::getPaymentMethodId, sellOneKeyReqDto.getPaymentInfoId());
        EzPaymentInfo paymentInfo = paymentInfoService.getOne(queryWrapper1);

        if (paymentInfo == null) {
            throw new BaseException(null, "801", "未匹配到支付方式", null);
        }
        //手续费计算
        BigDecimal fee = amount.multiply(ezSellConfig.getFeeRatio()).setScale(8, RoundingMode.FLOOR).add(ezSellConfig.getFee());
        //冻结用户卖出 数量
        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        BigDecimal total = amount.add(fee);
        b.setCoinName(sellOneKeyReqDto.getCoinName());
        b.setUserId(userId);
        b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b.setMainType(CoinConstants.MainType.FROZEN.getType());
        b.setFrozen(total);
        b.setFee(fee);
        b.setAvailable(amount.negate());
        b.setSonType(RecordSonType.TRANSACTION_FREEZE);
        cList.add(b);
        BalanceChange b2 = new BalanceChange();
        b2.setCoinName(sellOneKeyReqDto.getCoinName());
        b2.setUserId(userId);
        b2.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b2.setSonType(RecordSonType.HANDLING_FEE);
        b2.setAvailable(fee.negate());
        b2.setMainType(CoinConstants.MainType.FREE.getType());
        b2.setFee(BigDecimal.ZERO);
        cList.add(b2);
        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        //得到订单号
        String orderMatchNo = orderIndexService.getOrderNoByCountryCode("156", IndexOrderNoKey.ORDER_MATCH_INFO);
        EzOtcOrderPayment ezOtcOrderPayment = new EzOtcOrderPayment();
        ezOtcOrderPayment.setPaymentMethodId(paymentInfo.getPaymentMethodId());
        ezOtcOrderPayment.setRealName(paymentInfo.getRealName());
        ezOtcOrderPayment.setBankName(paymentInfo.getBankName());
        ezOtcOrderPayment.setAccountNumber(paymentInfo.getAccountNumber());
        ezOtcOrderPayment.setPaymentQrCode(paymentInfo.getPaymentQrCode());
        ezOtcOrderPayment.setType("1");
        ezOtcOrderPayment.setOrderMatchNo(orderMatchNo);
        orderPaymentService.save(ezOtcOrderPayment);

        Integer prompt = 20;
        Date beForeTime = DateUtils.getBeForeTime(prompt);
        //生成 订单（订单类型一键）
        EzOtcOrderMatch match = new EzOtcOrderMatch();
        match.setUserId(userId);
        match.setOrderMatchNo(orderMatchNo);
        match.setAdvertisingName("System shop");
        match.setPrice(ezSellConfig.getPrice());
        match.setAmount(amount);
        match.setCoinName(sellOneKeyReqDto.getCoinName());
        match.setTotalPrice(amount.multiply(ezSellConfig.getPrice()));
        match.setOrderPaymentId(ezOtcOrderPayment.getId());
        match.setOrderType("2");
        match.setType("0");
        match.setCreateTime(DateUtils.getNowDate());
        match.setStatus(MatchOrderStatus.WAITFORPAYMENT.getCode());
        match.setCurrencyCode("CNY");
        match.setIsAdvertising("1");
        match.setDueTime(beForeTime);
        this.save(match);

        BeanUtils.copyBeanProp(details, match);
        List<EzOtcOrderPayment> ezOtcOrderPayments = new ArrayList<>();
        ezOtcOrderPayments.add(ezOtcOrderPayment);
        details.setDueTime(beForeTime);
        details.setEzOtcOrderPayments(ezOtcOrderPayments);
        //TODO:将订单存入rabbitmq进行死信通信  时间到了就取消订单 根据卖家用户设置而定
        convertAndSend.convert(orderMatchNo, match.getStatus(), prompt);
        return Response.success("下单成功", details);//将订单返回
    }

    /**
     * 订单记录
     *
     * @param orderRecordQueryReqDto
     * @return
     */
    @Override
    public ResponseList<OrderRecordRespDto> orderRecord(OrderRecordQueryReqDto orderRecordQueryReqDto) {
        Page<EzOtcOrderMatch> page = new Page<EzOtcOrderMatch>(orderRecordQueryReqDto.getPage(), orderRecordQueryReqDto.getLimit());
        LambdaQueryWrapper<EzOtcOrderMatch> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderMatch::getUserId, ContextHandler.getUserId());
        queryWrapper.orderByDesc(EzOtcOrderMatch::getCreateTime);
        String status = orderRecordQueryReqDto.getStatus();
        CheckException.checkNotEmpty(status, () -> {
            queryWrapper.eq(EzOtcOrderMatch::getStatus, orderRecordQueryReqDto.getStatus());
        });
        String orderType = orderRecordQueryReqDto.getOrderType();
        CheckException.checkNotEmpty(orderType, () -> {
            queryWrapper.eq(EzOtcOrderMatch::getOrderType, orderRecordQueryReqDto.getOrderType());
        });
        String type = orderRecordQueryReqDto.getType();
        CheckException.checkNotEmpty(type, () -> {
            queryWrapper.eq(EzOtcOrderMatch::getType, orderRecordQueryReqDto.getType());
        });
        Page<EzOtcOrderMatch> ezOtcOrderMatchPage = baseMapper.selectPage(page, queryWrapper);
        List<OrderRecordRespDto> orderRecordRespDtos = new ArrayList<>();
        ezOtcOrderMatchPage.getRecords().forEach(e -> {
            OrderRecordRespDto orderRecordRespDto = new OrderRecordRespDto();
            BeanUtils.copyBeanProp(orderRecordRespDto, e);
            if (MatchOrderStatus.COMPLETED.getCode().equals(e.getStatus()) || MatchOrderStatus.PAID.getCode().equals(e.getStatus())){
                ArrayList<EzOtcOrderPayment> list = new ArrayList<>();
                list.add(paymentService.getById(e.getOrderPaymentId()));
                orderRecordRespDto.setEzOtcOrderPayments(list);
            }
            orderRecordRespDtos.add(orderRecordRespDto);
        });
        return ResponseList.success(orderRecordRespDtos);
    }

    /***
     * @Description: 广告订单匹配订单
     * @Param: [matchOrderQueryReqDto]
     * @return: com.ezcoins.response.ResponseList<com.ezcoins.project.otc.entity.resp.OrderRecordRespDto>
     * @Author: Wanglei
     * @Date: 2021/7/8
     * @param matchOrderQueryReqDto
     */
    @Override
    public ResponseList<OrderRecordRespDto> adMatchOrder(AdMatchOrderQueryReqDto matchOrderQueryReqDto) {
        Page<EzOtcOrderMatch> page = new Page<>(matchOrderQueryReqDto.getPage(), matchOrderQueryReqDto.getLimit());
        LambdaQueryWrapper<EzOtcOrderMatch> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderMatch::getOtcOrderUserId, ContextHandler.getUserId());

        String orderNo = matchOrderQueryReqDto.getOrderNo();
        if (StringUtils.isNotEmpty(orderNo)) {
            queryWrapper.eq(EzOtcOrderMatch::getOrderNo, orderNo);
        }
        String status = matchOrderQueryReqDto.getStatus();
        if ("1".equals(status)) {//已处理订单
            queryWrapper.and(wq -> wq.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.COMPLETED.getCode()).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.ORDERBEENCANCELLED.getCode()).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.COMPLETED.getCode()).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.REFUSE.getCode()));
        }
        if ("2".equals(status)) {//未处理
            queryWrapper.and(wq -> wq.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode()).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PAID.getCode()).or()
                    .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode()));
        }
        queryWrapper.orderByDesc(EzOtcOrderMatch::getCreateTime);
        Page<EzOtcOrderMatch> matchPage = baseMapper.selectPage(page, queryWrapper);
        List<EzOtcOrderMatch> records = matchPage.getRecords();
        List<OrderRecordRespDto> orderRecordRespDtos = new ArrayList<>();
        records.forEach(e -> {
            OrderRecordRespDto orderRespDto = new OrderRecordRespDto();
            BeanUtils.copyBeanProp(orderRespDto, e);
            LambdaQueryWrapper<EzOtcOrderPayment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(EzOtcOrderPayment::getOrderMatchNo, e.getOrderMatchNo());
            orderRespDto.setEzOtcOrderPayments(orderPaymentService.list(lambdaQueryWrapper));
            orderRecordRespDtos.add(orderRespDto);
        });
        return ResponseList.success(orderRecordRespDtos);
    }
}
