package com.ezcoins;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.project.coin.udun.HttpUtil;
import com.ezcoins.project.common.service.EmailService;
import com.ezcoins.project.otc.controller.EzOtcOrderController;
import com.ezcoins.project.otc.entity.EzOtcOrder;
import com.ezcoins.project.otc.entity.EzPaymentInfo;
import com.ezcoins.project.otc.service.EzOtcOrderIndexService;
import com.ezcoins.project.otc.service.EzPaymentInfoService;
import com.ezcoins.utils.HttpUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String s = HttpUtils.sendGet("https://api.huobi.pro/market/trade", "symbol=ethusdt");
        Map mapTypes = JSON.parseObject(s);
        System.out.println(mapTypes);

        JSONObject tick = (JSONObject)mapTypes.get("tick");
        System.out.println(tick);

        JSONArray data = (JSONArray)tick.get("data");
        System.out.println(data);

        JSONObject mapTypes2 = (JSONObject)data.get(0);
        BigDecimal price = (BigDecimal) mapTypes2.get("price");
        System.out.println(price);
    }

}
