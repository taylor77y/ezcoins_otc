package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 订单申诉
 * </p>
 *
 * @author wanglei
 * @since 2021-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzOtcCoinConfig对象", description="订单申诉")
public class EzOtcCoinConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "申诉id")
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

    @ApiModelProperty(value = "申诉id")
    private double maxTranscationAmount;

    @ApiModelProperty(value = "手续费")
    private double fee;

    @ApiModelProperty(value = "申诉id")
    private int status;

    @ApiModelProperty(value = "申诉id")
    private int stopSelling;

    @ApiModelProperty(value = "申诉id")
    private int prohibitionPurchase;
}
