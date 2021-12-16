package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="EzOtcCoinInfo对象", description="订单申诉")
public class EzOtcCoinExchange {

    /**
     * abbreviation : string
     * exchangeCoinId : 0
     * exchangeCoinName : string
     * exchangeValue : 0
     * id : 0
     * name : string
     * remark : string
     * updateDate : 2019-07-24T06:01:09.501Z
     */
    @ApiModelProperty(value = "申诉id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private int id;

    @ApiModelProperty(value = "申诉id")
    private String name;

    @ApiModelProperty(value = "申诉id")
    private String abbreviation;

    @ApiModelProperty(value = "申诉id")
    private int exchangeCoinId;

    @ApiModelProperty(value = "申诉id")
    private String exchangeCoinName;

    @ApiModelProperty(value = "申诉id")
    private String exchangeValue;

    @ApiModelProperty(value = "申诉id")
    private String remark;

    @ApiModelProperty(value = "申诉id")
    private String updateDate;
}
