package com.ezcoins.project.otc.task;

import com.ezcoins.utils.EthUsdtBtcUtils;
import com.ezcoins.utils.MoneyChangeUtils;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/6 15:06
 * @Version:1.0
 */
@Component
@Configurable
@EnableScheduling
@Slf4j
public class ScheduledTasks {

    public volatile static BigDecimal rmbPrice=BigDecimal.ZERO;

    public volatile static BigDecimal btcusdt=BigDecimal.ZERO;

    public volatile static BigDecimal ethusdt=BigDecimal.ZERO;


    /**每隔半小时**/
    @Scheduled(cron = "${scheduled.cron.rmbPrice}")
    public void upHotelStar(){
        BigDecimal usdToCNY = MoneyChangeUtils.getUSDToCNY();
        if (null!=usdToCNY){
            rmbPrice=usdToCNY;
            log.info("获取rmb - u 价格成功 ！！！--->{}",rmbPrice);
        }else {
            log.info("获取rmb - u 失败 ！！！--->{}",rmbPrice);
        }
    }
    public static HashMap<String,BigDecimal> getPrice(){
        btcusdt= EthUsdtBtcUtils.getEthOrBtcUsdt("btcusdt");
        ethusdt= EthUsdtBtcUtils.getEthOrBtcUsdt("ethusdt");
        HashMap<String,BigDecimal> map = new HashMap<>(2);
        map.put("btcusdt",btcusdt);
        map.put("ethusdt",ethusdt);
        return map;
    }

}
