package com.ezcoins.project.coin.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/9 15:21
 * @Version:1.0
 */
@Data
public class RechargeConfigReqDto {
    @ApiModelProperty(value = "冲币币种配置ID  id为null就是添加")
    private Integer id;

    @ApiModelProperty(value = "币种名")
    @NotBlank(message = "币种名不能为空")
    private String coinName;

    @ApiModelProperty(value = "币种全称")
    @NotBlank(message = "币种全称不能为空")
    private String coinFullName;

    @ApiModelProperty(value = "主币种类型")
    @NotBlank(message = "主币种类型不能为空")
    private String mainCoinType;

    @ApiModelProperty(value = "子币种类型")
    @NotBlank(message = "子币种类型不能为空")
    private String coinType;

}
