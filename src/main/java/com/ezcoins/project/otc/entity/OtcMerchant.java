package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class OtcMerchant {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "广告id")
    @TableId(value = "advertising_id", type = IdType.ASSIGN_ID)
    private String advertisingId;

    @ApiModelProperty(value = "数字币币种")
    private String digital_currency;

    @ApiModelProperty(value = "法币币种")
    private String fiat_currency;

    @ApiModelProperty(value = "最小收取数量")
    private String minimum_charge;

    @ApiModelProperty(value = "最大收取数量")
    private String maximum_charge;

    @ApiModelProperty(value = "单笔最小收取数量")
    private String minimum_single_charge;

    @ApiModelProperty(value = "单笔最大收取数量")
    private String maximum_single_charge;

    @ApiModelProperty(value = "汇率")
    private String exchange_rate;

    @ApiModelProperty(value = "剩余数量")
    private String remaining_amount;
}
