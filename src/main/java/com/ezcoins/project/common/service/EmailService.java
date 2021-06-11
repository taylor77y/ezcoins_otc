package com.ezcoins.project.common.service;


import com.ezcoins.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;


/**
 * @Author:
 * @Email:
 * @Description: 发送邮箱
 * @Date:2021/1/13 15:17
 * @Version:1.0
 */
@Service
@Deprecated
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private RedisCache redisCache;


    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;



    /**
     * @param mailBean: 邮件实体
     * @Description: 发送简单的邮件
     * @Author:
     * @Date: 2021/1/13 15:38
     * @return: void
     **/
    public void sendSimpleMail(MailBean mailBean) {
        taskExecutor.execute(() -> {
            String recName = mailBean.getRecName();
            String topic = mailBean.getTopic();
            String content = mailBean.getContent();
            String code = mailBean.getCode();
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(username);
                message.setTo(recName);
                message.setSubject(topic);
                message.setText(content+code);
                mailSender.send(message);
                log.info("邮箱发送成功{}",code);
            } catch (Exception e) {
                log.error("邮件发送失败{}", e.getMessage());
            }
        });
    }


}


