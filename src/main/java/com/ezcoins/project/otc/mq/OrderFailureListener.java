package com.ezcoins.project.otc.mq;

import com.ezcoins.mq.RabbitMQConfiguration;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

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

    @RabbitHandler
    public void process(String order, Message message, @Headers Map<String, Object> headers, Channel channel) throws IOException {

        log.info("【订单号】 - [{}]",  order);
        // 判断订单是否已经支付，如果支付则；否则，取消订单（逻辑代码省略)
 
        // 手动ack
//        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        // 手动签收
//        channel.basicAck(deliveryTag, false);
        System.out.println("执行结束....");
 
    }
}