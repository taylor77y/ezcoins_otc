package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.base.BaseException;
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
import com.ezcoins.project.otc.entity.*;
import com.ezcoins.project.otc.entity.req.AdMatchOrderQueryReqDto;
import com.ezcoins.project.otc.entity.req.OrderRecordQueryReqDto;
import com.ezcoins.project.otc.entity.req.SellOneKeyReqDto;
import com.ezcoins.project.otc.entity.resp.OrderRecordRespDto;
import com.ezcoins.project.otc.entity.resp.OtcInfoOrder;
import com.ezcoins.project.otc.mapper.EzOtcOrderMatchMapper;
import com.ezcoins.project.otc.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.response.BaseResponse;
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


    /***
     * @Description: 用户 取消订单（两个状态可取消订单  1：接单广告（卖家未接受订单）用户免费取消 2：接单广告/普通广告（用户未支付状态） 用户取消次数增加）
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/18
     * @param matchOrderNo
     */
    @Override
    public BaseResponse cancelOrder(String matchOrderNo) {
        String userId = ContextHandler.getUserId();
        //根据订单号查询到订单
        EzOtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        if ("0".equals(orderMatch.getType())){
            return BaseResponse.error(MessageUtils.message("该订单类型不能取消"));
        }
        //查看订单状态
        if (orderMatch.getStatus().equals(MatchOrderStatus.PENDINGORDER.getCode())) {
            //用户免费取消
            orderMatch.setStatus(MatchOrderStatus.ORDERBEENCANCELLED.getCode());
        } else if (orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            orderMatch.setStatus(MatchOrderStatus.CANCELLED.getCode());
            //将订单匹配数量增加回去
            EzOtcOrder ezOtcOrder = otcOrderService.getById(orderMatch.getOrderNo());
            ezOtcOrder.setQuotaAmount(ezOtcOrder.getQuotaAmount().subtract(orderMatch.getAmount()));
            otcOrderService.updateById(ezOtcOrder);
            //查询当前用户取消订单数量
            int count = 1;
            Object object = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId);
            if (null != object) {
                count =(Integer) object;
                count += 1; //用户取消次数增加
            }
            redisCache.setCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId, count, Math.toIntExact(DateUtils.getSecondsNextEarlyMorning()), TimeUnit.SECONDS);
        } else {
            throw new BaseException(MessageUtils.message("订单状态已发生变化"));
        }
        baseMapper.updateById(orderMatch);
        return BaseResponse.success();
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
    public BaseResponse confirmPayment(String matchOrderNo) {
        //根据订单号查询到订单
        EzOtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            return BaseResponse.error(MessageUtils.message("订单状态已发生变化"));
        }
        orderMatch.setPaymentTime(DateUtils.getNowDate());
        orderMatch.setStatus(MatchOrderStatus.PAID.getCode());
        //发送标记到页面
        //TODO:存入消息


        List<EzOtcChatMsg> list=new ArrayList<>();
        String userId = ContextHandler.getUserId();
        if ("0".equals(orderMatch.getType())){//买单
            EzOtcChatMsg ezOtcChatMsg1 = new EzOtcChatMsg();
            ezOtcChatMsg1.setOrderMatchNo(orderMatch.getOrderMatchNo());
            ezOtcChatMsg1.setSendText(SystemOrderTips.PAYMENT_SELL);
            ezOtcChatMsg1.setReceiveUserId(userId);
            list.add(ezOtcChatMsg1);

            EzOtcChatMsg ezOtcChatMsg2 = new EzOtcChatMsg();
            ezOtcChatMsg2.setOrderMatchNo(orderMatch.getOrderMatchNo());
            ezOtcChatMsg2.setSendText(SystemOrderTips.PAYMENT_BUY);
            ezOtcChatMsg2.setReceiveUserId(orderMatch.getOtcOrderUserId());
            list.add(ezOtcChatMsg2);
            WebSocketHandle.orderStatusChange(userId,MatchOrderStatus.PAID.getCode());
        }else {
            EzOtcChatMsg ezOtcChatMsg1 = new EzOtcChatMsg();
            ezOtcChatMsg1.setSendText(SystemOrderTips.PAYMENT_BUY);
            ezOtcChatMsg1.setReceiveUserId(userId);
            ezOtcChatMsg1.setOrderMatchNo(orderMatch.getOrderMatchNo());
            list.add(ezOtcChatMsg1);

            EzOtcChatMsg ezOtcChatMsg2 = new EzOtcChatMsg();
            ezOtcChatMsg2.setSendText(SystemOrderTips.PAYMENT_SELL);
            ezOtcChatMsg2.setOrderMatchNo(orderMatch.getOrderMatchNo());
            ezOtcChatMsg2.setReceiveUserId(orderMatch.getOtcOrderUserId());
            list.add(ezOtcChatMsg2);
            WebSocketHandle.orderStatusChange(orderMatch.getOtcOrderUserId(),MatchOrderStatus.PAID.getCode());
        }
        otcChatMsgService.saveBatch(list);
        baseMapper.updateById(orderMatch);
        return BaseResponse.success();
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
    public BaseResponse sellerPut(String matchOrderNo) {
        EzOtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            throw new BaseException(MessageUtils.message("订单状态已发生变化"));
        }
        //匹配订单分为 买单 和 卖单  //查询到otc 订单
        EzOtcOrder ezOtcOrder = otcOrderService.getById(orderMatch.getOrderNo());
        List<BalanceChange> cList = new ArrayList<>();
        //减少冻结的币
        BigDecimal frozeAmount = ezOtcOrder.getFrozeAmount();
        BigDecimal amount = orderMatch.getAmount();

        BalanceChange b1 = new BalanceChange();
        b1.setCoinName(orderMatch.getCoinName());
        b1.setAvailable(amount.negate());
        b1.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b1.setMainType(CoinConstants.MainType.TRANSFEROUT.getType());
        b1.setFee(BigDecimal.ZERO);
        BalanceChange b2 = new BalanceChange();
        b2.setCoinName(orderMatch.getCoinName());
        b2.setAvailable(amount);
        b2.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
        b2.setMainType(CoinConstants.MainType.TRANSFERIN.getType());
        b2.setFee(BigDecimal.ZERO);
        //改变OTC信息
        LambdaQueryWrapper<EzAdvertisingBusiness> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        String sellUserId=null;
        String buyUserId=null;
        Date payTime=orderMatch.getPaymentTime();

        EzOtcChatMsg ezOtcChatMsg = new EzOtcChatMsg();
        ezOtcChatMsg.setSendText(SystemOrderTips.RELEASE);
        ezOtcChatMsg.setOrderMatchNo(orderMatch.getOrderMatchNo());

        String receiveUserId=null;
        if ("0".equals(ezOtcOrder.getType())) {//买单
            b1.setUserId(orderMatch.getUserId());
            cList.add(b1);
            //将amount币存入买家的账户中
            b2.setUserId(ezOtcOrder.getUserId());
            sellUserId=orderMatch.getUserId();
            buyUserId=ezOtcOrder.getUserId();
            cList.add(b2);

            receiveUserId=orderMatch.getOtcOrderUserId();
        } else if ("1".equals(ezOtcOrder.getType())) {  //卖单 需要广告商户进行放币
            b1.setUserId(ezOtcOrder.getUserId());
            cList.add(b1);
            //otc订单冻结币的剩余
            BigDecimal frozeNow = frozeAmount.subtract(amount);
            ezOtcOrder.setFrozeAmount(frozeNow);
            //将amount币存入买家的账户中
            b2.setUserId(orderMatch.getUserId());
            sellUserId=ezOtcOrder.getUserId();
            buyUserId=orderMatch.getUserId();
            cList.add(b2);
            otcOrderService.updateById(ezOtcOrder);
            receiveUserId=orderMatch.getUserId();
        }
        ezOtcChatMsg.setReceiveUserId(receiveUserId);
        WebSocketHandle.orderStatusChange(receiveUserId,MatchOrderStatus.COMPLETED.getCode());
        otcChatMsgService.save(ezOtcChatMsg);

        Date nowDate = DateUtils.getNowDate();
        //改变订单状态
        orderMatch.setStatus(MatchOrderStatus.COMPLETED.getCode());
        orderMatch.setFinishTime(nowDate);
        baseMapper.updateById(orderMatch);
        businessService.updateCount(sellUserId,buyUserId,payTime,nowDate);
        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        return BaseResponse.success();
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


    /**
     * 一键卖币 (只支持人民币)
     *
     * @param sellOneKeyReqDto
     * @return
     */
    @Override
    public BaseResponse sellOneKey(SellOneKeyReqDto sellOneKeyReqDto) {
        //查看是否有未完成的的订单
        LambdaQueryWrapper<EzOtcOrderMatch> matchLambdaQueryWrapper = new LambdaQueryWrapper<>();
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getUserId, ContextHandler.getUserId());
        matchLambdaQueryWrapper.eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.PENDINGORDER.getCode()).or()
                .eq(EzOtcOrderMatch::getStatus, MatchOrderStatus.WAITFORPAYMENT.getCode());
        EzOtcOrderMatch orderMatch = baseMapper.selectOne(matchLambdaQueryWrapper);

        if (orderMatch != null) {
            return BaseResponse.error("请先完成当前未完成的订单")
                    .data("orderMatchNo", orderMatch.getOrderMatchNo());
        }
        LambdaQueryWrapper<EzOneSellConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOneSellConfig::getCoinName, sellOneKeyReqDto.getCoinName());
        EzOneSellConfig ezSellConfig = sellConfigService.getOne(queryWrapper);
        if ("1".equals(ezSellConfig.getStatus())) {
            return BaseResponse.error(MessageUtils.message("当前币种一键卖币已关闭"));
        }

        BigDecimal amount = sellOneKeyReqDto.getAmount();
        BigDecimal maxAmount = ezSellConfig.getMaxAmount();
        BigDecimal minAmount = ezSellConfig.getMinAmount();

        if (amount.compareTo(maxAmount) > 0 || amount.compareTo(minAmount) < 0) {
            return BaseResponse.error(MessageUtils.message("输入数量错误"));
        }
        //手续费计算
        BigDecimal fee = amount.multiply(ezSellConfig.getFeeRatio()).setScale(8, RoundingMode.FLOOR).add(ezSellConfig.getFee());

        //冻结用户卖出 数量
        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        String userId = ContextHandler.getUserId();
        BigDecimal total = amount.add(fee);
        b.setAvailable(total);
        b.setCoinName(sellOneKeyReqDto.getCoinName());
        b.setUserId(userId);
        b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b.setFrozen(total);
        b.setFee(fee);
        b.setMainType(CoinConstants.MainType.FROZEN.getType());
        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        //生成 订单（订单类型一键）
        EzOtcOrderMatch match = new EzOtcOrderMatch();
        //得到订单号
        String orderMatchNo = orderIndexService.getOrderNoByCountryCode("156", IndexOrderNoKey.ORDER_MATCH_INFO);
        match.setOrderMatchNo(orderMatchNo);
        match.setAdvertisingName("System shop");
        match.setPrice(ezSellConfig.getPrice());
        match.setAmount(amount);
        match.setCoinName(sellOneKeyReqDto.getCoinName());
        match.setTotalPrice(amount.multiply(ezSellConfig.getPrice()));
        match.setOrderPaymentId(sellOneKeyReqDto.getPaymentInfoId());
        match.setOrderType("2");
        match.setType("1");
        return BaseResponse.success();
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
