package com.ezcoins;

import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.handler.AuthenticationInterceptor;
import com.ezcoins.project.common.service.EmailService;
import com.ezcoins.project.otc.task.ScheduledTasks;
import com.ezcoins.utils.MoneyChangeUtils;
import com.ezcoins.websocket.WebSocketHandle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/6 14:56
 * @Version:1.0
 */
@Order(1)
@Component
@Slf4j
public class Statr implements CommandLineRunner {

    @Autowired
    EmailService emailService;

    @Override
    public void run(String... args) throws Exception {
        AuthenticationInterceptor.flag = false;
        sync();
        AuthenticationInterceptor.flag = true;
    }
    private void sync() throws Exception {
        ScheduledTasks.rmbPrice = MoneyChangeUtils.getUSDToCNY();
        if (ScheduledTasks.rmbPrice!=null){
            log.info("启动成功");
        }
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        HashMap<String, BigDecimal> price = ScheduledTasks.getPrice();
                        price.put("usdtrmb",ScheduledTasks.rmbPrice);
                        WebSocketHandle.price(price);
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}