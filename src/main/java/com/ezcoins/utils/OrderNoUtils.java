package com.ezcoins.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Author: WangLei  订单号工具类
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/18 10:09
 * @Version:1.0
 */
public class OrderNoUtils {
    /**
     * 获取订单号
     * @return
     */
    public static String getOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }
}
