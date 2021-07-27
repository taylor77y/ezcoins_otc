package com.ezcoins.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/6 15:16
 * @Version:1.0t5
 */
public class EthUsdtBtcUtils {
    private EthUsdtBtcUtils(){}
    public static BigDecimal getEthOrBtcUsdt(String symbol){
        String s = HttpUtils.sendGet("https://api.huobi.pro/market/trade", "symbol="+symbol);
        Map mapTypes = JSON.parseObject(s);
        JSONObject tick = (JSONObject)mapTypes.get("tick");
        JSONArray data = (JSONArray)tick.get("data");
        JSONObject mapTypes2 = (JSONObject)data.get(0);
        return (BigDecimal) mapTypes2.get("price");
    }
}
