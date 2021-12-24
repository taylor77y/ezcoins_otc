package com.ezcoins.project.otc.entity.resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;

public class OtcTransactionRespDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "otc交易id")
    @TableId(value = "transaction_id", type = IdType.ASSIGN_ID)
    private String transactionId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "设备来源")
    private String deviceSource;

    @ApiModelProperty(value = "发起时间")
    private String launchTime;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;
}
