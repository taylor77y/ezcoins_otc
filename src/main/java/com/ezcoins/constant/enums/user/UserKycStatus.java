package com.ezcoins.constant.enums.user;

/**
 * @Author:
 * @Email:
 * @Description:  2拒绝 1通过 0待审核
 * @Date:2021/5/27 18:44
 * @Version:1.0
 */
public enum UserKycStatus {
    VERIFIED("0", "已认证"),
    NOTCERTIFIED("1", "未认证");

    private final String code;
    private final String info;

    UserKycStatus(String code, String info)
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
