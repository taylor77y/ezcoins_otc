package com.ezcoins.project.consumer.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/31 14:04
 * @Version:1.0
 */
@Data
public class SePasswordReqDto {
    @ApiModelProperty(value = "1：手机 2：邮箱号",required = true)
    private String type;

    @ApiModelProperty(value = "验证码",required = true)
    @NotBlank(message = "{请输入验证码}")
    private String code;

    /** 新密码**/
    @ApiModelProperty(value = "新密码",required = true)
    @NotBlank(message = "{请输入要修改的密码}")
    private String newPassword;
}

