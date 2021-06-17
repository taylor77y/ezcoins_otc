package com.ezcoins.constant.enums.coin;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/17 14:30
 * @Version:1.0
 */
public enum WithdrawOrderStatus {
    FAIL("-1","提币失败"),
    PENDINGREVIEW("1", "待审核"),
    BY("2", "审核通过"),
    REFUSE("3", "提币拒绝");

    private final String code;
    private final String info;

    WithdrawOrderStatus(String code, String info)
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
