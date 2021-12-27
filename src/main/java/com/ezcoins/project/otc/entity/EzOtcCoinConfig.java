package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *      所有数字货币挂单配置表
 * </p>
 *
 * @author wanglei
 * @since 2021-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ez_otc_coin_config")
@ApiModel(value="EzOtcCoinConfig对象", description="所有 coin 挂单配置信息表")
public class EzOtcCoinConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private int id;

    @ApiModelProperty(value = "币种id")
    private int coinId;

    @ApiModelProperty(value = "币种名称")
    private String coinName;

    @ApiModelProperty(value = "交易类型：在线购买、卖出")
    private String transactionType;

    @ApiModelProperty(value = "十进制金额")
    private int amountDecimal;

    @ApiModelProperty(value = "卖出广告最低发布数量")
    private double minTranscationAmount;

    @ApiModelProperty(value = "最大交易数量")
    private double maxTranscationAmount;

    @ApiModelProperty(value = "手续费")
    private double fee;

    @ApiModelProperty(value = "状态 （0启用 1禁用 ）")
    private short status;

    @ApiModelProperty(value = "停止销售（0启用 1禁用 ）")
    private int stopSelling;

    @ApiModelProperty(value = "禁止购买（0启用 1禁用 ）")
    private int prohibitionPurchase;
}
