package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/28 16:16
 * @Version:1.0
 */
@Data
public class SellOneKeyReqDto {

    @ApiModelProperty(value = "币种")
    private String coinName;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "收款方式ID")
    private String id;


}
