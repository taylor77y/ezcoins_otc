package com.ezcoins.project.otc.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/25 14:38
 * @Version:1.0
 */
@Data
public class OtcInfoOrder {
    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "最小限额")
    private BigDecimal minimumLimit;

    @ApiModelProperty(value = "最大限额")
    private BigDecimal maximumLimit;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "剩余数量")
    private BigDecimal lastAmount;

    @ApiModelProperty(value = "是否为接单广告(0:是 1：否)")
    private String isAdvertising;

    @ApiModelProperty(value = "国际货币")
    private String currencyCode;

}
