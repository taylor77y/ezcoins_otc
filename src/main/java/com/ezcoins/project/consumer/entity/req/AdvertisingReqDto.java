package com.ezcoins.project.consumer.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/30 13:53
 * @Version:1.0
 */
@Data
public class AdvertisingReqDto {
    @ApiModelProperty(value = "保证金",required = true)
    @NotNull(message = "{请输入保证金}")
    private BigDecimal margin;

    private String userId;
}
