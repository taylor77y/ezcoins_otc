package com.ezcoins.constant.enums.user;

/**
 * @Author:
 * @Email:
 * @Description:  2拒绝 1通过 0待审核
 * @Date:2021/5/27 18:44
 * @Version:1.0
 */
public enum KycStatus {
    PENDINGREVIEW("0", "待审核"),
    BY("1", "通过"),
    REFUSE("2", "拒绝");

    private final String code;
    private final String info;

    KycStatus(String code, String info)
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
