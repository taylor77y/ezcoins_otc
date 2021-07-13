package com.ezcoins.project.common.service;


import com.ezcoins.constant.EmailConstant;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * @Author:
 * @Email:
 * @Description: 发送邮箱
 * @Date:2021/1/13 15:17
 * @Version:1.0
 */
@Service
@Slf4j
public class EmailService {

    @Resource
    private JavaMailSenderImpl mailSender;

    @Autowired
    private RedisCache redisCache;


    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    private static final Integer TIMEOUT = 5;


    public void sendComplexMail(String key, String recName, String topic, String code) {
        taskExecutor.execute(() -> {
            //复杂邮件
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            try {
                //邮件发送助手
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                //邮件设置
                helper.setSubject(topic);  //使用 HTML 格式，true
                helper.setText(String.format(EmailConstant.content, MessageUtils.message("您好！您正在进行邮箱验证，本次请求的验证码为："), code, MessageUtils.message("为了保障您帐号的安全性，请在{0}分钟内完成验证", TIMEOUT), MessageUtils.message("感谢选择亿智交易平台")), true);
                //接收者
                helper.setTo(recName);
                //发送者
                helper.setFrom(username);
                //上传附件：文件名、文件路径
                mailSender.send(mimeMessage);
                redisCache.setCacheObject(key + recName, code, TIMEOUT, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("邮件发送失败{}", e.getMessage());
            }
        });
    }
}


