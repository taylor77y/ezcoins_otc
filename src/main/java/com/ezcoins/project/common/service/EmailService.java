package com.ezcoins.project.common.service;


import com.ezcoins.constant.enums.EmailConstant;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
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

    @Value("${ezcoins.profile}")
    private String profile;

    @Value("${ezcoins.emailLog}")
    private String emailLog;

    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    private static final Integer TIMEOUT = 5;


    public void sendComplexMail(String key, String recName, String topic, String code) {
        taskExecutor.execute(() -> {
            //复杂邮件
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            try {
//
//                helper.setFrom(from);// 发件人
//                helper.setTo(toUsers);// 收件人
//                //helper.setCc(ccUsers);//抄送人，使用Cc还是Bcc大家根据具体场景自己选择
//                helper.setBcc(ccUsers);//秘密抄送（发件人，收件人，抄送人都不会看到抄送给谁了）
//                helper.setSubject(title);// 标题
//                /* 创建html内容，若不创建html标签,则图片会以附件的形式发送，而并非直接以内容显示 */
//                String content = "<html><body>" + text + "<img src=\'cid:" + imgId + "\'></img>" + "</body></html>";
//                helper.setText(content, true);// text：内容，true:为HTML邮件（false则为普通文本邮件）
//                File file = new File(imgPath);// 创建图片文件
//                FileSystemResource resource = new FileSystemResource(file);
//                helper.addInline(imgId, resource);
//                mailSender.send(mimeMessage);// 发送邮件
                //邮件发送助手
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                //发送者
                helper.setFrom(new InternetAddress(username,"EzCoins","UTF-8"));
                //接收者
                helper.setTo(recName);
                //邮件设置
                helper.setSubject(topic);  //使用 HTML 格式，true
                String format = String.format(EmailConstant.content, MessageUtils.message("您好！您正在进行邮箱验证，本次请求的验证码为："), code, MessageUtils.message("为了保障您帐号的安全性，请在{0}分钟内完成验证", TIMEOUT), MessageUtils.message("感谢选择亿智交易平台"));
                helper.setText(format, true);
                File file = new File(profile+"/"+emailLog);// 创建图片文件
                FileSystemResource resource = new FileSystemResource(file);
                helper.addInline("123", resource);
                //上传附件：文件名、文件路径
                mailSender.send(mimeMessage);
                redisCache.setCacheObject(key + recName, code, TIMEOUT, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("邮件发送失败{}", e.getMessage());
            }
        });
    }
}


