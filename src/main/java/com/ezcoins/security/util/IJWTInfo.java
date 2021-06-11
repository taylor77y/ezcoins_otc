package com.ezcoins.security.util;


public interface IJWTInfo {
    /**
     * 获取用户名
     * @return
     */
    String getUserName();

    /**
     * 获取用户ID
     * @return
     */
    String getUserId();

    /**
     * 获取名称
     * @return
     */
    String getUserType();
}
