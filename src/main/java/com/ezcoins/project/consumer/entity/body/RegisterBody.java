package com.ezcoins.project.consumer.entity.body;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterBody {

    @ApiModelProperty(value = "用户名")
    @NotBlank(message ="{用户名不能为空}")
    private String userName;

    @ApiModelProperty(value = "手机号")
    @NotBlank(message ="{手机号不能为空}")
    private String phone;


    @ApiModelProperty(value = "密码")
    @NotBlank(message ="{密码不能为空}")
    private String password;


    @ApiModelProperty(value = "验证码")
    @NotBlank(message ="{验证码不能为空}")
    private String code;

    @ApiModelProperty(value = "邀请码")
    private String inviteCode;

    @ApiModelProperty(value = "安全密码")
    @NotBlank(message = "{安全密码不能为空}")
    private String securityPassword;

}
