package com.ezcoins.project.common.mq.service;

import com.ezcoins.constant.enums.otc.MatchOrderStatus;
import com.ezcoins.mq.RabbitMQConfiguration;
import lombok.Data;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/5 18:20
 * @Version:1.0
 */
@Service
public class ConvertAndSendService {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    public void convert(String orderMatchNo,String status,Integer exTime){
        this.rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.orderExchange, //发送至订单交换机
                RabbitMQConfiguration.routingKeyOrder, //订单定routingKey
                orderMatchNo + "_" + status //订单号   这里可以传对象 比如直接传订单对象
                , message -> {
                    message.getMessageProperties().setExpiration(1000 * 60 * exTime +"");
                    return message;
                });
    }

}
