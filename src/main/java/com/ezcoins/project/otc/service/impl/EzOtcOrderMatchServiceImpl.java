package com.ezcoins.project.otc.service.impl;

import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.otc.entity.EzOtcOrder;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.ezcoins.project.otc.mapper.EzOtcOrderMatchMapper;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.service.EzOtcOrderService;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        if (orderMatch.getStatus().equals(MatchOrderStatus.PENDINGORDER.getCode())){
            //用户免费取消
            orderMatch.setStatus(MatchOrderStatus.ORDERBEENCANCELLED.getCode());
        }else if (orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())){
            orderMatch.setStatus(MatchOrderStatus.CANCELLED.getCode());

            //查询当前用户取消订单数量
            int count=1;
            if (null!=redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY+userId)){
                count=redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY+userId);
                count+=1; //用户取消次数增加
            }
            redisCache.setCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY+userId,count, Math.toIntExact(DateUtils.getSecondsNextEarlyMorning()), TimeUnit.SECONDS);
        }else {
            throw new BaseException("订单状态已发生变化");
        }
        return BaseResponse.success();
    }


    /**
     * @Description: 买家确认 付款
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/19
     * @param matchOrderNo
     */
    @Override
    public BaseResponse confirmPayment(String matchOrderNo) {
        //根据订单号查询到订单
        EzOtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())){
            return BaseResponse.error("订单状态已发生变化");
        }
        orderMatch.setPaymentTime(DateUtils.getNowDate());
        orderMatch.setStatus(MatchOrderStatus.PAID.getCode());

        //发送标记到页面
        baseMapper.updateById(orderMatch);
        return BaseResponse.success();
    }




    /**
     * @Description: 卖家放款
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/19
     * @param matchOrderNo
     */
    @Override
    public BaseResponse sellerPut(String matchOrderNo) {
        EzOtcOrderMatch orderMatch = baseMapper.selectById(matchOrderNo);
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.PAID.getCode())
                || !orderMatch.getStatus().equals(MatchOrderStatus.WAITFORPAYMENT.getCode())){
            return BaseResponse.error("订单状态已发生变化");
        }
        //匹配订单分为 买单 和 卖单

        //卖单 需要广告商户进行放币

        //查询到otc 订单
        EzOtcOrder ezOtcOrder = otcOrderService.getById(orderMatch.getOrderNo());
        //减少冻结的币
        BigDecimal frozeAmount = ezOtcOrder.getFrozeAmount();
        BigDecimal amount = orderMatch.getAmount();

        //减少账户冻结的币


        //otc订单冻结币的剩余
        BigDecimal frozeNow = frozeAmount.subtract(amount);
        ezOtcOrder.setFrozeAmount(frozeNow);

        //将amount币存入买家的账户中





        //卖单


        return null;
    }






}
