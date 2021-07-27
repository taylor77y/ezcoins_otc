package com.ezcoins.project.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Slf4j
public class MailFeedbackService {

    private JavaMailSenderImpl mailSender;

//    @PostConstruct
//    public void buildMailSender() {
//        // 邮件发送者
//        mailSender = new JavaMailSenderImpl();
//        mailSender.setDefaultEncoding("UTF-8");
//        mailSender.setHost("mail.reevo.life");
//        mailSender.setPort(465);
//        mailSender.setProtocol("smtp");
//        mailSender.setUsername("noreply@reevo.life");
//        mailSender.setPassword("ping@888$");
//        Properties javaMailProperties = new Properties();
//        javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        javaMailProperties.put("mail.smtp.socketFactory.port", "465");
//        javaMailProperties.put("mail.smtp.ssl.enable", "true");
//        javaMailProperties.put("mail.debug", "false");
//        mailSender.setJavaMailProperties(javaMailProperties);
//    }

    @PostConstruct
    public void buildMailSende2r() {
        // 邮件发送者
        mailSender = new JavaMailSenderImpl();
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setHost("smtpout.secureserver.net");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setUsername("support@ezcoins.cc");
        mailSender.setPassword("P@ssw0rdEzcoins");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.put("mail.smtp.socketFactory.port", "465");
        javaMailProperties.put("mail.smtp.ssl.enable", "false");
        javaMailProperties.put("mail.debug", "true");
        mailSender.setJavaMailProperties(javaMailProperties);
    }

    /**
     * 发送html格式的邮件
     *
     * @param subject 主题
     * @param content 内容
     */
    public void sendHtmlMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("support@ezcoins.cc");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("html格式邮件发送失败");
        }
    }

    /**
     * 发送商家注册的反馈邮件
     *
     * @param to
     */
    public void sendShopRegisterMail(String to) {
        String content = "<html>\n" +
                "\t<head>\n" +
                "\t\t<meta charset=\"utf-8\">\n" +
                "\t\t<title></title>\n" +
                "\t</head>\n" +
                "\t<body>\n" +
                "\t\t<p>欢迎来到REEVO!</p>\n" +
                "\t\t&#12288;我们为此深表荣幸并且期待与您共同建立一个更强大与专业的团队。<br />\n" +
                "\t\t&#12288;为了使您的提款功能通畅无阻，请务必完成您的商业认证。审批可能需要 2-3 个工作日（星期一至星期五，不包括周末与公共假期）。 请注意，您必须在十四天内完成您的商业认证以避免户口被中止。<br />\n" +
                "\t\t<p style=\"text-align: right;\">如需要任何协助，请联系我们的客服</p>\n" +
                "\t\t<p style=\"text-align: right;\">support@reevo.life</p>\n" +
                "\t\t<p style=\"text-align: right;\">谢谢！</p>\n" +
                "\t</body>\n" +
                "</html>";
        sendHtmlMail(to, "欢迎来到REEVO!", content);
    }
}