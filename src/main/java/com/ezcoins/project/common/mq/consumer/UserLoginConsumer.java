package com.ezcoins.project.common.mq.consumer;

import com.ezcoins.project.system.entity.EzSysLogininfor;
import com.ezcoins.project.system.service.EzSysLogininforService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/4 19:09
 * @Version:1.0
 */
@Slf4j
@Component
@RabbitListener(queues = "fanout_userlogin_queue")
public class UserLoginConsumer {

    @Autowired
    private EzSysLogininforService ezSysLogininforService;

    @RabbitHandler
    public void process(EzSysLogininfor ezSysLogininfor) {
        try {
            ezSysLogininforService.save(ezSysLogininfor);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
