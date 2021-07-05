package com.ezcoins;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.project.common.service.EmailService;
import com.ezcoins.project.otc.controller.EzOtcOrderController;
import com.ezcoins.project.otc.entity.EzOtcOrder;
import com.ezcoins.project.otc.entity.EzPaymentInfo;
import com.ezcoins.project.otc.service.EzOtcOrderIndexService;
import com.ezcoins.project.otc.service.EzPaymentInfoService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EzcoinsParentApplicationTests {
    @Autowired
    EmailService emailService;

    @Autowired
    EzOtcOrderIndexService orderNoService;

    @Autowired
    private EzPaymentInfoService paymentInfoService;


    @Test
    void contextLoads() {
//        emailService. sendComplexMail(new MailBean("1044508403@qq.com","主题","内容","123456"));
        Integer paymentMethod1 = 3;
        Integer paymentMethod2 = 1;
        Integer paymentMethod3 = null;
        LambdaQueryWrapper<EzPaymentInfo> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(EzPaymentInfo::getUserId, "1411267129485856770");
        queryWrapper1.eq(EzPaymentInfo::getPaymentMethodId, paymentMethod1).or().
                eq(EzPaymentInfo::getPaymentMethodId, paymentMethod2).or().
                eq(EzPaymentInfo::getPaymentMethodId, paymentMethod3);
        List<EzPaymentInfo> list = paymentInfoService.list(queryWrapper1);
        System.out.println(list);
    }

}
