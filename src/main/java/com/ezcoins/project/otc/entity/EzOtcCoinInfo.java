package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("ez_internet_account")
@ApiModel(value="EzOtcCoinInfo对象", description="所有可用 coin 信息")
public class EzOtcCoinInfo {

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private int id;

    @ApiModelProperty(value = "数字货币中文名",required = true)
    private String name;

    @ApiModelProperty(value = "数字货币英文名",required = true)
    private String englishName;

    @ApiModelProperty(value = "数字货币英文缩写名",required = true)
    private String englishShortName;

    @ApiModelProperty(value = "数字货币类型id")
    private int coinTypeId;

    @ApiModelProperty(value = "数字货币类型名")
    private String coinTypeName;

    @ApiModelProperty(value = "类型")
    private String type;
}
