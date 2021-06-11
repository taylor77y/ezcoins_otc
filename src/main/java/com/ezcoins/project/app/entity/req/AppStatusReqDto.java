package com.ezcoins.project.app.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/7 18:06
 * @Version:1.0
 */
@Data
public class AppStatusReqDto {

    @ApiModelProperty(value = "是否上架(1:下架 0:上架 )",required = true)
    @NotBlank(message = "{上下架不能为空}")
    private String isRacking;

    @ApiModelProperty(value = "版本编号",required = true)
    @NotBlank(message = "{版本编号不能为空}")
    private String id;


    @ApiModelProperty(value = "平台(0:Android 1:IOS)",required = true)
    @NotBlank(message = "{平台不能为空}")
    private String platform;
}
