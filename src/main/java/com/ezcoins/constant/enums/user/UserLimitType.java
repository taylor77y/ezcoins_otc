package com.ezcoins.constant.enums.user;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/10 11:13
 * @Version:1.0
 */
public enum UserLimitType{
    LOGIN_LIMIT("0", "登录封禁");

    private final String code;
    private final String info;

    UserLimitType(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }


}
