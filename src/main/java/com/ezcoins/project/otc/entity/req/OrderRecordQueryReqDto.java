package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/29 10:21
 * @Version:1.0
 */
@Data
public class OrderRecordQueryReqDto {
    @ApiModelProperty(value = "广告类型(0:买  1：卖)")
    private String type;

    @ApiModelProperty(value = "订单状态(1:接单已取消 2:待接单 3：已取消 4：等待支付 5：已支付 6：已完成)")
    private String status;

    @ApiModelProperty(value = "订单类型（1：普通 2：一键）")
    private String orderType;

    @ApiModelProperty(value = "页码")
    private Long page;

    @ApiModelProperty(value = "页容量")
    private Long limit;
}
