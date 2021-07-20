package com.ezcoins;

import com.ezcoins.project.coin.udun.Address;
import com.ezcoins.project.coin.udun.ResponseMessage;
import com.ezcoins.project.coin.wallet.cc.WalletClientService;
import com.ezcoins.project.common.service.EmailService;
import com.ezcoins.project.otc.service.EzOtcOrderIndexService;
import com.ezcoins.utils.HttpUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EzcoinsParentApplicationTests {
    @Autowired
    EmailService emailService;

    @Autowired
    WalletClientService walletClient;

    @Test
    void contextLoads() throws Exception {
//        Address address = walletClient.createAddressList("test");
        walletClient.transfer("1","1","1","0x58d9e6d57ce65ef5dcfc1b22022c7a866573e99b","test", BigDecimal.ONE);
//        System.out.println(address);
    }

}
