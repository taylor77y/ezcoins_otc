package com.ezcoins.project.consumer.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/9 15:01
 * @Version:1.0
 */
@Data
public class EzUserReqDto {

    @ApiModelProperty(value = "用户编号")
    private String userId;

    @ApiModelProperty(value = "用户名",required = true)
    @NotBlank(message = "{用户名不能为空}")
    private String userName;

    @ApiModelProperty(value = "密码",required = true)
    @NotBlank(message = "{密码不能为空}")
    private String password;

    @ApiModelProperty(value = "电话",required = true)
    @NotBlank(message = "{电话不能为空}")
    private String phone;


    @ApiModelProperty(value = "验证码",required = true)
    @NotBlank(message = "{验证码不能为空}")
    private String code;


    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "邀请码")
    private String inviteCode;

    @ApiModelProperty(value = "安全密码",required = true)
    @NotBlank(message = "{安全密码不能为空}")
    private String securityPassword;


    @ApiModelProperty(value = "备注")
    private String remark;

}
