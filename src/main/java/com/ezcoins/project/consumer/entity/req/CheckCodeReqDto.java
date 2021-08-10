package com.ezcoins.project.consumer.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/8/2 14:14
 * @Version:1.0
 */
@Data
public class CheckCodeReqDto {
    @ApiModelProperty(value = "电话国际区号")
    private String phoneArea;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "验证码",required = true)
    @NotBlank(message = "{code.not}")
    private String code;

    @ApiModelProperty(value = "验证方式  1：手机验证  2：邮箱验证",required = true)
    private String type;

    @ApiModelProperty(value = "邮箱")
    private String email;

    /** 验证码用于类型  1：注册 2：找回密码*/
    @ApiModelProperty(value = "验证码用于类型 1：注册 2：找回密码  3：绑定安全信息 4：修改安全密码 ",required = true)
    private String captchaType;


}
