package com.ezcoins.project.acl.entity.req;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class JwtAuthenticationRequest implements Serializable {

    private static final long serialVersionUID = -8445943548965154778L;

    @ApiModelProperty(value = "用户名",required = true)
    @NotBlank(message = "{用户名不能为空}")
    private String username;

    @ApiModelProperty(value = "菜单id",required = true)
    @NotBlank(message = "{密码不能为空}")
    private String password;


    public JwtAuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public JwtAuthenticationRequest() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
