package com.ezcoins.project.otc.task;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/29 21:01
 * @Version:1.0
 */
@Data
public class ToUsdtPrice {
    private String name;
    private BigDecimal price;

}
