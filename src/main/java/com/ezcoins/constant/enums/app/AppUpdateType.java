package com.ezcoins.constant.enums.app;

/**
 * @author Administrator
 */

public enum AppUpdateType {
    ONSHELF("0", "上架"), OFFSHELF("1", "下架");

    private final String code;
    private final String info;

    AppUpdateType(String code, String info)
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
