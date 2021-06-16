package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/15 16:27
 * @Version:1.0
 */
@Data
public class PaymentQrcodeTypeReqDto {
    @ApiModelProperty(value = "类型 2：支付宝  3：微信 .....")
    private int paymentMethodId;

    @ApiModelProperty(value = "支付宝表编号")
    private String id;

    @ApiModelProperty(value = "卖家姓名")
    private String realName;

    @ApiModelProperty(value = "账号")
    private String accountNumber;

    @ApiModelProperty(value = "付款二维码")
    private String paymentQrCode;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)")
    private String status;


}
