package com.ezcoins.constant.enums.user;

/**
 * 用户状态
 * 
 * 
 */
public enum UserStatus{
    OK("0", "正常"), DELETED("1", "停用");

    private final String code;
    private final String info;

    UserStatus(String code, String info)
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
