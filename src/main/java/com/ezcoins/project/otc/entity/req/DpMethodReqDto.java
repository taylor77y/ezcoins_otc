package com.ezcoins.project.otc.entity.req;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/15 17:51
 * @Version:1.0
 */
@Data
public class DpMethodReqDto {
    @ApiModelProperty(value = "收款方式",required = true)
    private String paymentMethod;

    @ApiModelProperty(value = "编号",required = true)
    private String id;
}
