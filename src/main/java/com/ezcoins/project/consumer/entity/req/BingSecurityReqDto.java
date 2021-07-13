package com.ezcoins.project.consumer.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/12 18:34
 * @Version:1.0
 */
@Data
public class BingSecurityReqDto {
    @ApiModelProperty(value = "1：手机 2：邮箱号",required = true)
    private String type;

    @ApiModelProperty(value = "手机或邮箱号",required = true)
    @NotBlank(message = "{请输入手机号或邮箱号}")
    private String phoneOrEmail;

    @ApiModelProperty(value = "验证码",required = true)
    @NotBlank(message = "{请输入验证码}")
    private String code;

    @ApiModelProperty(value = "电话国际区号")
    private String phoneArea;
}
