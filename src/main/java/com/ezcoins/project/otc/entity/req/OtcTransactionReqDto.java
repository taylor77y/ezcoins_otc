package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OtcTransactionReqDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "otc交易id")
    private String transactionId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "设备来源")
    private String deviceSource;

    @ApiModelProperty(value = "发起时间")
    private String launchTime;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "页码")
    private Long page;

    @ApiModelProperty(value = "页容量")
    private Long limit;
}
