package com.ezcoins.constant.enums.user;

public enum AdvertisingStatus {
    PENDINGREVIEW("0", "待审核"),
    BY("1", "通过"),
    REFUSE("2", "拒绝");

    private final String code;
    private final String info;

    AdvertisingStatus(String code, String info)
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
