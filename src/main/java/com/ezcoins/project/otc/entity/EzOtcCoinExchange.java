package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ezcoins.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ez_otc_coin_exchange")
@ApiModel(value="EzOtcCoinExchange对象", description="通过币种id和法币名称获取法币兑换值")
public class EzOtcCoinExchange extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
    @ApiModelProperty(value = "币种id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private int id;

    @ApiModelProperty(value = "申诉id")
    private String name;

    @ApiModelProperty(value = "法币名称")
    private String abbreviation;

    @ApiModelProperty(value = "币种id")
    private int exchangeCoinId;

    @ApiModelProperty(value = "申诉id")
    private String exchangeCoinName;

    @ApiModelProperty(value = "法币兑换值")
    private String exchangeValue;

//    @ApiModelProperty(value = "申诉id")
//    private String remark;

//    @ApiModelProperty(value = "申诉id")
//    private String updateDate;
}
