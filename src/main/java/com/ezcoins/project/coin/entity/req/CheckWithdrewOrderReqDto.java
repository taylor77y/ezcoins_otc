package com.ezcoins.project.coin.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/17 14:24
 * @Version:1.0
 */
@Data
public class CheckWithdrewOrderReqDto {

    @ApiModelProperty(value = "提币订单id",required = true)
    @NotBlank(message = "{提币订单编号不能为空}")
    private String id;

    @ApiModelProperty(value = "操作 (2：审核通过 3：提币拒绝)",required = true)
    @NotBlank(message = "{操作类型不能为空}")
    private String operate;

    @ApiModelProperty(value = "失败原因")
    private String reason;
}
