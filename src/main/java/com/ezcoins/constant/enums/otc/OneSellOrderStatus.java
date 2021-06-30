package com.ezcoins.constant.enums.otc;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/28 18:07
 * @Version:1.0 1:待接单  2:拒接订单  3：等待支付 4：已付款 5：已完成
 */
public enum OneSellOrderStatus {
    PENDINGORDER("1", "待接单"),
    REFUSE("2", "接单拒绝"),
    CANCELLED("3", "等待支付"),
    WAITFORPAYMENT("4", "已支付"),
    PAID("5", "已完成");

    private final String code;
    private final String info;
    OneSellOrderStatus(String code, String info){
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
