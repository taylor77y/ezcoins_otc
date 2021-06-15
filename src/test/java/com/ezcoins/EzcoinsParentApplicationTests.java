package com.ezcoins;

import com.ezcoins.project.common.service.EmailService;
import com.ezcoins.project.common.service.MailBean;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EzcoinsParentApplicationTests {
    @Autowired
    EmailService emailService;


    @Test
    void contextLoads() {
//        emailService. sendComplexMail(new MailBean("1044508403@qq.com","主题","内容","123456"));
    }

}
