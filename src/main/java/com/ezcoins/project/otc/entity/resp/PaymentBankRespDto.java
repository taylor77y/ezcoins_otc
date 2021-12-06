package com.ezcoins.project.otc.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PaymentBankRespDto {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "卖家姓名")
    private String realName;

    @ApiModelProperty(value = "银行名")
    private String bankName;

    @ApiModelProperty(value = "账户名")
    private String accountName;

    @ApiModelProperty(value = "账户号")
    private String number;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)")
    private String status;
}
