package com.ezcoins.project.coin.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/5 19:07
 * @Version:1.0
 */
@Data
public class ReviseAccountReqDto {
    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名",required = true)
    private String userId;

    @ApiModelProperty(value = "/增加：1  减少：0",required = true)
    private String  type;//增加：1  减少：0

    @ApiModelProperty(value = "原因",required = true)
    @NotBlank(message = "请输入原因")
    private String memo;

    @ApiModelProperty(value = "数量",required = true)
    private BigDecimal amount;


    @ApiModelProperty(value = "币种名",required = true)
    private String coinName;

}
