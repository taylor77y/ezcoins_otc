package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ezcoins.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *       次级菜单-OTC订单
 * </p>
 *
 * @author taylor
 * @since 2021-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("otc_merchant")
@ApiModel(value="OtcMerchant对象", description="次级菜单-商家列表")
public class OtcMerchant extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "广告id")
    @TableId(value = "advertising_id", type = IdType.ASSIGN_ID)
    private String advertisingId;

    @ApiModelProperty(value = "数字币币种")
    private String digitalCurrency;

    @ApiModelProperty(value = "法币币种")
    private String fiatCurrency;

    @ApiModelProperty(value = "最小收取数量")
    private String minCharge;

    @ApiModelProperty(value = "最大收取数量")
    private String maxCharge;

    @ApiModelProperty(value = "单笔最小收取数量")
    private String minSingleCharge;

    @ApiModelProperty(value = "单笔最大收取数量")
    private String maxSingleCharge;

    @ApiModelProperty(value = "汇率")
    private String exchangeRate;

    @ApiModelProperty(value = "剩余数量")
    private String remainingAmount;
}
