package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/23 10:56
 * @Version:1.0
 */
@Data
public class PageQuery {
    @ApiModelProperty(value = "页码")
    private Long page;

    @ApiModelProperty(value = "页容量")
    private Long limit;

    @ApiModelProperty(value = "未处理订单数量  0:显示未处理订单  1：不显示处理订单")
    private String isHandle;

}
