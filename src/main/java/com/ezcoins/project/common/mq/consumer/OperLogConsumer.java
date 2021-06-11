package com.ezcoins.project.common.mq.consumer;

import com.ezcoins.project.system.entity.EzSysLog;
import com.ezcoins.project.system.service.EzSysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 记录日志的消费者
 * @author wanglei
 */
@Slf4j
@Component
@RabbitListener(queues = "fanout_operlog_queue")
public class OperLogConsumer {
    @Autowired
    private EzSysLogService ezSysLogService;

    @RabbitHandler
    public void process(EzSysLog ezSysLog) {
        try {
            ezSysLogService.save(ezSysLog);
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }
}