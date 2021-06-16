package com.ezcoins.constant.enums.otc;

public enum PaymentMethod {

    ALIPAY("1", "支付宝"), WECHAT("2", "微信"), BANK("3", "银行");
    private final String code;
    private final String info;
    PaymentMethod(String code, String info)
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
