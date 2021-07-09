package com.ezcoins.project.coin.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/9 15:33
 * @Version:1.0
 */
@Data
public class RwStatusReqDto {
    @ApiModelProperty(value = "配置id",required = true)
    @NotBlank(message = "配置id不能为空")
    private String id;

    @ApiModelProperty(value = "操作 (0开放 1：关闭)",required = true)
    @NotBlank(message = "操作类型不能为空")
    private String operate;
}
