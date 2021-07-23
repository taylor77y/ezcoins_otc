package com.ezcoins.constant.enums.otc;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description: 1:接单已取消 2:待接单 3：已取消 4：等待支付 5：已支付 6：已完成
 * @Date:2021/6/18 14:44
 * @Version:1.0
 */
public enum MatchOrderStatus {
    ORDERBEENCANCELLED("1", "接单已取消"),
    PENDINGORDER("2", "待接单"),
    CANCELLED("3", "已取消"),
    WAITFORPAYMENT("4", "等待支付"),
    PAID("5", "等待确认到款"),
    COMPLETED("6", "已完成"),
    REFUSE("7", "接单拒绝");
    private final String code;
    private final String info;
    MatchOrderStatus(String code, String info){
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
