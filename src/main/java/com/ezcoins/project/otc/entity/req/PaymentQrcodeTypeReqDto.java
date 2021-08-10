package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/15 16:27
 * @Version:1.0
 */
@Data
public class PaymentQrcodeTypeReqDto {
    @ApiModelProperty(value = "详情编号  null为添加 否则：修改")
    private String id;

    @ApiModelProperty(value = "类型 1:银行 2：支付宝  3：微信")
    private int paymentMethodId;

    @ApiModelProperty(value = "开户银行名")
    private String bankName;

    @ApiModelProperty(value = "银行地址")
    private String optional;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "账号")
    @NotBlank(message = "{accountNumber.not}")
    private String accountNumber;

    @ApiModelProperty(value = "付款二维码")
    private String paymentQrCode;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)")
    private String status;
}
