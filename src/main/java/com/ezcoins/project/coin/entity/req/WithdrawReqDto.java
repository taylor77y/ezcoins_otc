package com.ezcoins.project.coin.entity.req;

import com.ezcoins.aspectj.lang.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/2 17:07
 * @Version:1.0
 */
@Data
public class WithdrawReqDto {

    @ApiModelProperty(value = "提币地址",required = true)
    @NotBlank(message = "{提币地址不能为空}")
    private String toAddress;

    @ApiModelProperty(value = "币种名",required = true)
    private String coinName;


    @ApiModelProperty(value = "数量",required = true)
    private BigDecimal amount;

}
