package com.ezcoins.constant.enums.coin;

/**
 * @Author: WangLei 币种状态（0启用 1禁用 ）
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/17 15:29
 * @Version:1.0
 */
public enum CoinStatus {
    ENABLE("0", "启用"),
    DISABLE("1", "禁用");

    private final String code;
    private final String info;

    CoinStatus(String code, String info)
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
