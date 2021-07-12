package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/12 14:08
 * @Version:1.0
 */
@Data
public class DoOrderReqDto {
    @ApiModelProperty(value = "匹配订单号")
    @NotBlank(message = "匹配订单号不能为空")
    private String orderMatchNo;

    @ApiModelProperty(value = "申诉操作： 0：放行 1：订单失败)")
    @NotBlank(message = "请先选择放行还是订单失败")
    private String status;

}
