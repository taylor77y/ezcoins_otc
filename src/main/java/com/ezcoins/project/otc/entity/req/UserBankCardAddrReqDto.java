package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserBankCardAddrReqDto {

    @ApiModelProperty(value = "id null：增加  不为null：修改")
    private String id;

    @ApiModelProperty(value = "银行名")
    private String bankName;

    @ApiModelProperty(value = "账户名")
    private String accountName;

    @ApiModelProperty(value = "账户号")
    private String number;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活)")
    private String status;
}
