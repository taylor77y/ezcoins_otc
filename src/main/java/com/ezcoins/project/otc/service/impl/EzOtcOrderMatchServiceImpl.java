package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezcoins.base.BaseException;
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
import com.ezcoins.project.otc.entity.EzOneSellConfig;
import com.ezcoins.project.otc.entity.EzOneSellOrder;
import com.ezcoins.project.otc.entity.EzOtcOrder;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
    private EzPaymentInfoService paymentInfoService;

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
            if (null != redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId)) {
                count = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId);
                count += 1; //用户取消次数增加
            }
            redisCache.setCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId, count, Math.toIntExact(DateUtils.getSecondsNextEarlyMorning()), TimeUnit.SECONDS);
        } else {
            throw new BaseException("订单状态已发生变化");
        }
        baseMapper.updateById(orderMatch);
        return BaseResponse.success();
    }


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
            return BaseResponse.error("订单状态已发生变化");
        }
        orderMatch.setPaymentTime(DateUtils.getNowDate());
        orderMatch.setStatus(MatchOrderStatus.PAID.getCode());

        //发送标记到页面
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
        if (!orderMatch.getStatus().equals(MatchOrderStatus.PAID.getCode())
                || !orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            throw new BaseException("订单状态已发生变化");
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
        b1.setMainType(CoinConstants.MainType.SELL.getType());
        b1.setFee(BigDecimal.ZERO);

        BalanceChange b2 = new BalanceChange();
        b2.setCoinName(orderMatch.getCoinName());
        b2.setAvailable(amount);
        b2.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
        b2.setMainType(CoinConstants.MainType.BUY.getType());
        b2.setFee(BigDecimal.ZERO);
        //卖单 需要广告商户进行放币
        if ("0".equals(ezOtcOrder.getType())) {//买单
            b1.setUserId(orderMatch.getUserId());
            cList.add(b1);
            //将amount币存入买家的账户中
            b2.setUserId(ezOtcOrder.getUserId());
            cList.add(b2);
        } else if ("1".equals(ezOtcOrder.getType())) {//卖单
            b1.setUserId(ezOtcOrder.getUserId());
            cList.add(b1);

            //otc订单冻结币的剩余
            BigDecimal frozeNow = frozeAmount.subtract(amount);
            ezOtcOrder.setFrozeAmount(frozeNow);

            //将amount币存入买家的账户中
            b2.setUserId(orderMatch.getUserId());
            cList.add(b2);
            otcOrderService.updateById(ezOtcOrder);
        }

        //改变订单状态
        orderMatch.setStatus(MatchOrderStatus.COMPLETED.getCode());
        baseMapper.updateById(orderMatch);
        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        return BaseResponse.success();
    }

    /**
     * @Description:
     * @Param: [userId]
     * @return: com.ezcoins.response.Response<com.ezcoins.project.otc.entity.resp.OtcInfoOrder>
     * @Author: Wanglei
     * @Date: 2021/6/25
     * @param userId
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
        LambdaQueryWrapper<EzOneSellConfig> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOneSellConfig::getCoinName,sellOneKeyReqDto.getCoinName());
        EzOneSellConfig ezSellConfig = sellConfigService.getOne(queryWrapper);
        if ("1".equals(ezSellConfig.getStatus())){
            return BaseResponse.error("当前币种一键卖币已关闭");
        }

        BigDecimal amount = sellOneKeyReqDto.getAmount();
        BigDecimal maxAmount = ezSellConfig.getMaxAmount();
        BigDecimal minAmount = ezSellConfig.getMinAmount();

        if (amount.compareTo(maxAmount)>0 || amount.compareTo(minAmount)<0){
            return BaseResponse.error("输入数量错误");
        }

        //手续费计算
        BigDecimal fee=amount.multiply(ezSellConfig.getFeeRatio()).setScale(8, RoundingMode.FLOOR).add(ezSellConfig.getFee());

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
        accountService.balanceChangeSYNC(cList);

        //生成 订单（订单类型一键）
        EzOtcOrderMatch match = new EzOtcOrderMatch();
        //得到订单号
        String orderMatchNo = orderIndexService.getOrderNo("156", IndexOrderNoKey.ORDER_MATCH_INFO);
        match.setOrderMatchNo(orderMatchNo);
        match.setAdvertisingName("app收币商铺");
        match.setPrice(ezSellConfig.getPrice());
        match.setAmount(amount);
        match.setCoinName(sellOneKeyReqDto.getCoinName());
        match.setTotalPrice(amount.multiply(ezSellConfig.getPrice()));

        return BaseResponse.success();
    }

    /**
     * 订单记录
     * @param orderRecordQueryReqDto
     * @return
     */
    @Override
    public ResponseList<OrderRecordRespDto> orderRecord(OrderRecordQueryReqDto orderRecordQueryReqDto) {
        Page<EzOtcOrderMatch> page=new Page<EzOtcOrderMatch>(orderRecordQueryReqDto.getPage(),orderRecordQueryReqDto.getLimit());
        LambdaQueryWrapper<EzOtcOrderMatch> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzOtcOrderMatch::getUserId,ContextHandler.getUserId());

        String status = orderRecordQueryReqDto.getStatus();
        CheckException.checkNotEmpty(status, () -> {
            queryWrapper.eq(EzOtcOrderMatch::getStatus,orderRecordQueryReqDto.getStatus());
        });
        String orderType = orderRecordQueryReqDto.getOrderType();
        CheckException.checkNotEmpty(orderType, () -> {
            queryWrapper.eq(EzOtcOrderMatch::getOrderType,orderRecordQueryReqDto.getOrderType());
        });
        String type = orderRecordQueryReqDto.getType();
        CheckException.checkNotEmpty(type, () -> {
            queryWrapper.eq(EzOtcOrderMatch::getType,orderRecordQueryReqDto.getType());
        });
        Page<EzOtcOrderMatch> ezOtcOrderMatchPage = baseMapper.selectPage(page, queryWrapper);
        List<OrderRecordRespDto> orderRecordRespDtos = new ArrayList<>();

        ezOtcOrderMatchPage.getRecords().forEach(e->{
            OrderRecordRespDto orderRecordRespDto = new OrderRecordRespDto();
            BeanUtils.copyBeanProp(orderRecordRespDto,e);
            if (!e.getStatus().equals(MatchOrderStatus.CANCELLED.getCode())
                    || !e.getStatus().equals(MatchOrderStatus.ORDERBEENCANCELLED.getCode())
            || !e.getStatus().equals(MatchOrderStatus.REFUSE.getCode())){

                orderRecordRespDto.setPaymentInfo(paymentInfoService.getById(e.getPaymentInfoId()));

            }
            orderRecordRespDtos.add(orderRecordRespDto);
        });
        return ResponseList.success(orderRecordRespDtos);
    }
}
