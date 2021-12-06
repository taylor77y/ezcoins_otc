package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserInternetAccountReqDto {

    @ApiModelProperty(value = "id null：增加  不为null：修改")
    private String id;

    @ApiModelProperty(value = "网络账号姓名")
    private String internetAccountName;

    @ApiModelProperty(value = "网络账号号码")
    private String internetAccountNumber;

    @ApiModelProperty(value = "网络账号类型")
    private String internetAccountType;
}
