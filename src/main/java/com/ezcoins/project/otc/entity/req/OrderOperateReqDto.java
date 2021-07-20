package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/18 17:21
 * @Version:1.0
 */
@Data
public class OrderOperateReqDto {
    @ApiModelProperty(value = "订单号",required = true)
    private String matchOrderNo;

    @ApiModelProperty(value = "操作  （1：拒接  2：接受）",required = true)
    private String operate;

}
