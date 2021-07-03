package com.ezcoins.project.otc.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/3 20:18
 * @Version:1.0
 */
@Data
public class OrderPaymentRspDto {
    @ApiModelProperty(value = "开户银行名")
    private String bankName;

    @ApiModelProperty(value = "卖家姓名")
    private String realName;

    @ApiModelProperty(value = "付款二维码")
    private String paymentQrCode;

    @ApiModelProperty(value = "账号")
    private String accountNumber;

    @ApiModelProperty(value = "收款二维码类型：1:银行 2：支付宝 3：微信")
    private Integer paymentMethodId;
}
