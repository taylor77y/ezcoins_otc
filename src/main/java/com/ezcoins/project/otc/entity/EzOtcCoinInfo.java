package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="EzOtcCoinInfo对象", description="所有可用 coin 信息")
public class EzOtcCoinInfo {

    @ApiModelProperty(value = "申诉id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private int id;

    @ApiModelProperty(value = "币种id")
    private String name;

    @ApiModelProperty(value = "币种id")
    private String englishName;

    @ApiModelProperty(value = "币种id")
    private String englishShortName;

    @ApiModelProperty(value = "币种id")
    private int coinTypeId;

    @ApiModelProperty(value = "币种id")
    private String coinTypeName;

    @ApiModelProperty(value = "币种id")
    private String type;
}
