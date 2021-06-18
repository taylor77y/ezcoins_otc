package com.ezcoins.project.otc.entity.req;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/18 13:57
 * @Version:1.0
 */
@Data
public class PlaceOrderReqDto {

    private String orderNo;//订单号

    private BigDecimal amount;//购买/卖出 数量

    private BigDecimal securityPassword;//资金密码

}
