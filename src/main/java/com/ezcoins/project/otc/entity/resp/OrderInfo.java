package com.ezcoins.project.otc.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/22 18:15
 * @Version:1.0
 */
@Data
public class OrderInfo {
    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "交易提示")
    private String tradingTips;

    @ApiModelProperty(value = "支付方式")
    private List<String> icons;

    @ApiModelProperty(value = "OTC商户名")
    private String name;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "最小限额")
    private BigDecimal minimumLimit;

    @ApiModelProperty(value = "最大限额")
    private BigDecimal maximumLimit;


    @ApiModelProperty(value = "剩余数量")
    private BigDecimal lastAmount;


}
