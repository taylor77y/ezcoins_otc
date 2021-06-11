package com.ezcoins.security;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录对象
 * 
 */
@Data
public class LoginBody{
    /**
     * 用户名
     */
    @NotBlank(message = "{user.name.is.empty}")//用户名不能为空
    private String username;
    /**
     * 用户密码
     */
    @NotBlank(message = "{user.password.is.empty}")//密码不能为空
    private String password;

}
