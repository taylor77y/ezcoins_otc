package com.ezcoins.project.otc.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzOtcOrder对象", description="")
public class EzOtcOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单号")
    @TableId(value = "orderNo")
    private String orderNo;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "总数量")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "匹配数量")
    private BigDecimal quotaAmount;

    @ApiModelProperty(value = "冻结数量")
    private BigDecimal frozeAmount;

    @ApiModelProperty(value = "国际货币")
    private String currencyCode;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "最小限额")
    private BigDecimal minimumLimit;

    @ApiModelProperty(value = "最大限额")
    private BigDecimal maximumLimit;

    @ApiModelProperty(value = "广告类型(0:买  1：卖)")
    private String type;

    @ApiModelProperty(value = "是否为接单广告(0:是 1：否)")
    private String isAdvertising;

    @ApiModelProperty(value = "支付方式1")
    private String paymentMethod1;

    @ApiModelProperty(value = "支付方式2")
    private String paymentMethod2;

    @ApiModelProperty(value = "支付方式3")
    private String paymentMethod3;

    @ApiModelProperty(value = "付款期限(分钟)")
    private Integer prompt;

    @ApiModelProperty(value = "交易提示")
    private String tradingTips;

    @ApiModelProperty(value = "订单状态")
    private String status;

    @ApiModelProperty(value = "订单完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "乐观锁")
    @Version
    private Integer version;
}
