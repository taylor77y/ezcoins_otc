package com.ezcoins.project.otc.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ezcoins.constant.SystemOrderTips;
import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.mq.RabbitMQConfiguration;
import com.ezcoins.project.otc.entity.EzOtcChatMsg;
import com.ezcoins.project.otc.entity.EzOtcOrder;
import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.ezcoins.project.otc.service.EzOtcChatMsgService;
import com.ezcoins.project.otc.service.EzOtcOrderMatchService;
import com.ezcoins.project.otc.service.EzOtcOrderService;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.StringUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: 订单失效监听器
 * @author: nxq email: niuxiangqian163@163.com
 * @createDate: 2020/12/18 8:30 上午
 * @updateUser: nxq email: niuxiangqian163@163.com
 * @updateDate: 2020/12/18 8:30 上午
 * @updateRemark:
 * @version: 1.0
 **/
@Component
@Slf4j
@RabbitListener(queues = RabbitMQConfiguration.dealQueueOrder)
public class OrderFailureListener {

    @Autowired
    private EzOtcOrderMatchService matchService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private EzOtcOrderService otcOrderService;

    @Autowired
    private EzOtcChatMsgService otcChatMsgService;

    @RabbitHandler
    public void process(String order, Message message, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        log.info("【订单号】 - [{}]", order);
        String[] splitContent = new String[2];
        if (StringUtils.isNotEmpty(order)) {
            if (order.contains("_")) {
                splitContent = order.split("_");
            }
        }
        //截取字符串
        String otcOrderMatchNo = splitContent[0];
        String status = splitContent[1];

        //取消订单
        if (status.equals(MatchOrderStatus.WAITFORPAYMENT.getCode())) {
            EzOtcOrderMatch match = matchService.getById(otcOrderMatchNo);
            if (match==null){
                return;
            }
            match.setStatus(MatchOrderStatus.CANCELLED.getCode());
            matchService.updateById(match);
            //将订单匹配数量增加回去
            EzOtcOrder ezOtcOrder = otcOrderService.getById(match.getOrderNo());
            if (ezOtcOrder==null){
                return;
            }
            ezOtcOrder.setQuotaAmount(ezOtcOrder.getQuotaAmount().subtract(match.getAmount()));
            otcOrderService.updateById(ezOtcOrder);
            //查询当前用户取消订单数量
            int count = 1;
            Object object = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + match.getUserId());
            if (null != object) {
                count = (Integer) object + 1;
            }
            redisCache.setCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + match.getUserId(), count, Math.toIntExact(DateUtils.getSecondsNextEarlyMorning()), TimeUnit.SECONDS);

            List<EzOtcChatMsg> list=new ArrayList<>();
            EzOtcChatMsg ezOtcChatMsg1 = new EzOtcChatMsg();
            EzOtcChatMsg ezOtcChatMsg2 = new EzOtcChatMsg();
            ezOtcChatMsg1.setOrderMatchNo(match.getOrderMatchNo());
            ezOtcChatMsg1.setReceiveUserId(match.getUserId());
            ezOtcChatMsg1.setSendText(SystemOrderTips.SYSTEM_CANCEL);

            ezOtcChatMsg2.setOrderMatchNo(match.getOrderMatchNo());
            ezOtcChatMsg2.setReceiveUserId(match.getOtcOrderUserId());
            ezOtcChatMsg2.setSendText(SystemOrderTips.SYSTEM_CANCEL_2);

            list.add(ezOtcChatMsg1);
            list.add(ezOtcChatMsg2);
            otcChatMsgService.sendSysChat(list, MatchOrderStatus.COMPLETED.getCode());

        }
        //接单广告取消
        if (status.equals(MatchOrderStatus.PENDINGORDER.getCode())) {
            EzOtcOrderMatch match = matchService.getById(otcOrderMatchNo);
            if (match==null){
                return;
            }
            match.setStatus(MatchOrderStatus.ORDERBEENCANCELLED.getCode());
            matchService.updateById(match);
            List<EzOtcChatMsg> list=new ArrayList<>();
            EzOtcChatMsg ezOtcChatMsg1 = new EzOtcChatMsg();
            EzOtcChatMsg ezOtcChatMsg2 = new EzOtcChatMsg();
            ezOtcChatMsg1.setOrderMatchNo(match.getOrderMatchNo());
            ezOtcChatMsg1.setReceiveUserId(match.getUserId());
            ezOtcChatMsg1.setSendText(SystemOrderTips.SYSTEM_CANCEL);
            ezOtcChatMsg2.setOrderMatchNo(match.getOrderMatchNo());
            ezOtcChatMsg2.setReceiveUserId(match.getOtcOrderUserId());
            ezOtcChatMsg2.setSendText(SystemOrderTips.SYSTEM_CANCEL_2);
            list.add(ezOtcChatMsg1);
            list.add(ezOtcChatMsg2);
            otcChatMsgService.sendSysChat(list, MatchOrderStatus.ORDERBEENCANCELLED.getCode());

        }
        System.out.println("执行结束....");
    }
}