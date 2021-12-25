package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("otc_fiat_currency")
@ApiModel(value="OtcFiatCurrency对象", description="次级菜单-OTC法币币种")
public class OtcFiatCurrency {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private long id;

    @ApiModelProperty(value = "数字货币中文名")
    private String currencyCode;

    @ApiModelProperty(value = "数字货币英文名")
    private String currencySymbol;

    @ApiModelProperty(value = "数字货币中文名")
    private String currencyScale;

    @ApiModelProperty(value = "数字货币英文名")
    private String countryCode;

    @ApiModelProperty(value = "数字货币英文名")
    private String iconUrl;
}
