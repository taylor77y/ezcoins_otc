package com.ezcoins;

import com.ezcoins.project.common.service.EmailService;
import com.ezcoins.project.otc.controller.EzOtcOrderController;
import com.ezcoins.project.otc.entity.EzOtcOrder;
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
//        emailService. sendComplexMail(new MailBean("1044508403@qq.com","主题","内容","123456"));
        System.out.println(orderNoService.getOrderNo("86","order_info"));
    }

}
