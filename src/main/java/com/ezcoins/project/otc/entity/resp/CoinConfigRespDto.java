package com.ezcoins.project.otc.entity.resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CoinConfigRespDto {

    @ApiModelProperty(value = "申诉id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private int id;

    @ApiModelProperty(value = "申诉id")
    private int coinId;

    @ApiModelProperty(value = "申诉id")
    private String coinName;

    @ApiModelProperty(value = "申诉id")
    private int amountDecimal;

    @ApiModelProperty(value = "申诉id")
    private double minTranscationAmount;

    @ApiModelProperty(value = "申诉id")
    private double maxTranscationAmount;

    @ApiModelProperty(value = "申诉id")
    private double fee;

    @ApiModelProperty(value = "申诉id")
    private int status;

    @ApiModelProperty(value = "申诉id")
    private int stopSelling;

    @ApiModelProperty(value = "申诉id")
    private int prohibitionPurchase;
}
