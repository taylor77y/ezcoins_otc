package com.ezcoins.constant;


/**
 * @author Administrator
 */
public interface SystemOrderTips {
    String PLACE_ORDER_SUCCESS ="您已成功下单，请及时支付";

    String PLACE_ORDER_SUCCESS_SELL ="您已成功下单，请等待买方付款";

    String SYSTEM_CANCEL="您的订单以为超时已被系统取消，如有疑问，请联系客服";

    String REFUSE_ORDER="接单已被对方拒绝。如有疑问，请联系客服";

    String ORDERS_TIPS="您已下单，正在等待对方接单。对方接单前请勿付款，为保证交易顺利安全进行，卖家可能需要您提供银行流水等资料，您可与对方沟通一致后提供";

    String RELEASE="对方已确认收到您的付款，您所购买的USDT已发放到您的账户,请查收";

    String PAYMENT="您已将订单标记为“已付款”的状态，请等待对方确认收款和放行";

    String CANCEL="订单已取消。如有疑问，请联系客服";
}

