package com.ezcoins.project.otc.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/30 17:34
 * @Version:1.0
 */
@Data
public class ReleaseOrderRespDto {
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "支付方式")
    private List<String> icons;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "剩余数量")
    private BigDecimal lastAmount;

    @ApiModelProperty(value = "最小限额")
    private BigDecimal minimumLimit;

    @ApiModelProperty(value = "最大限额")
    private BigDecimal maximumLimit;

    @ApiModelProperty(value = "广告类型(0:买  1：卖)")
    private String type;

    @ApiModelProperty(value = "是否为接单广告(0:是 1：否)")
    private String isAdvertising;


}
