package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/1 10:27
 * @Version:1.0
 */
@Data
public class AdMatchOrderQueryReqDto {

    @ApiModelProperty(value = "已完成订单：1  待处理订单 2")
    private String status;

    @ApiModelProperty(value = "发布单号")
    private String orderNo;

    @ApiModelProperty(value = "页码")
    private Long page;

    @ApiModelProperty(value = "页容量")
    private Long limit;
}
