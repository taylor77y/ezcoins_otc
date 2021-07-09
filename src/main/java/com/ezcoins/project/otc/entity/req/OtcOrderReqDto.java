package com.ezcoins.project.otc.entity.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/17 16:49
 * @Version:1.0
 */
@Data
public class OtcOrderReqDto {

    @ApiModelProperty(value = "用户id 后台发布订单时加上")
    private String userId;

    @ApiModelProperty(value = "单价",required = true)
    @NotNull(message = "{价格不能为空}")
    private BigDecimal price;

    @ApiModelProperty(value = "总数量")
    @NotNull(message = "{数量不能为空}")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "国际货币")
    @NotNull(message = "{国际货币不能为空}")
    private String currencyCode;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "最小限额")
    @NotNull(message = "{最小限额不能为空}")
    private BigDecimal minimumLimit;

    @ApiModelProperty(value = "最大限额")
    @NotNull(message = "{最大限额不能为空}")
    private BigDecimal maximumLimit;

    @ApiModelProperty(value = "订单类型(0:买  1：卖)")
    private String type;

    @ApiModelProperty(value = "是否为接单广告(0:是 1：否)")
    private String isAdvertising;

    @ApiModelProperty(value = "支付方式1")
    private Integer paymentMethod1;

    @ApiModelProperty(value = "支付方式2")
    private Integer paymentMethod2;

    @ApiModelProperty(value = "支付方式3")
    private Integer paymentMethod3;

    @ApiModelProperty(value = "付款期限(分钟)")
    @NotNull(message = "{付款期限不能为空}")
    private Integer prompt;

    @ApiModelProperty(value = "交易备注")
    private String tradingTips;
}
