package com.ezcoins;

import com.ezcoins.project.common.service.EmailService;
import com.ezcoins.project.otc.service.EzOtcOrderIndexService;
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

    @Autowired
    EzOtcOrderIndexService orderNoService;

    @Test
    void contextLoads() {
        emailService.sendComplexMail("123","1044508403@qq.com","感谢您使用亿智交易平台","123");

        try {
            Thread.sleep(1000*60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
