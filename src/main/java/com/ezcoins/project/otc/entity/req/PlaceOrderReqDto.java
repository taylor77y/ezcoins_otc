package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "订单号")
    private String orderNo;//订单号

    @ApiModelProperty(value = "//购买/卖出 数量")
    private BigDecimal amount;//购买/卖出 数量


}
