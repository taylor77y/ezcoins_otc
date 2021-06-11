package com.ezcoins.security;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/4 17:16
 * @Version:1.0
 */
@Data
public class TokenUserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 权限列表
     */
    private Set<String> permissions;

}
