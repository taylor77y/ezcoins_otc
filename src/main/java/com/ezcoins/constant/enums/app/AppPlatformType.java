package com.ezcoins.constant.enums.app;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/5 18:33
 * @Version:1.0
 */
public enum AppPlatformType {
    Android("0", "Android"), IOS("1", "IOS");
    private final String code;
    private final String info;
    AppPlatformType(String code, String info)
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
