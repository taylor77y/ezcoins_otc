package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/22 13:55
 * @Version:1.0
 */
@Data
public class OtcOrderQueryReqDto {
    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "交易金额")
    private String transactionAmount;

    @ApiModelProperty(value = "支付方式id")
    private String payMethodId;

    @ApiModelProperty(value = "国际货币code")
    private String currencyCode;

    @ApiModelProperty(value = "广告类型(0:买  1：卖)")
    private String type;

    @ApiModelProperty(value = "是否为接单广告(0:是 1：否)")
    private String isAdvertising;

    @ApiModelProperty(value = "页码")
    private Long page;

    @ApiModelProperty(value = "页容量")
    private Long limit;

}
