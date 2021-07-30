package com.ezcoins.constant;


/**
 * @author Administrator
 */
public interface SystemOrderTips {
    String PLACE_ORDER_SUCCESS ="您已成功下单，请及时支付";
    String ORDER_SUCCESS="对方已下单成功，等待对方支付成功";

    String PLACE_ORDER_SUCCESS_SELL ="您已成功下单，请等待买方付款";
    String ORDER_SUCCESS_SELL="对方下单，请及时付款";

    String PAYMENT_BUY="您已将订单标记为“已付款”的状态，请等待对方确认收款和放行";
    String PAYMENT_SELL="对方已将订单标记为“已付款”的状态，请确认收款和放行";

    String RELEASE_BUY="您已经确定收到到付，货币已转出到买家";
    String RELEASE_SELL="对方已确认收到您的付款，您所购买的货币已发放到您的账户,请查收";

    String CANCEL="订单已取消。如有疑问，请联系客服";//主动取消的一方
    String CANCEL_2="对方已取消订单";//被取消的一方


    String APPEAL_CANCEL="审核结果为取消订单";
    String APPEAL_PUT="审核结果为放行";




    String ORDERS_CANCEL="您已取消订单";


    String SYSTEM_CANCEL="您的订单以为超时已被系统取消，如有疑问，请联系客服";
    String SYSTEM_CANCEL_2="订单处理已超时，该订单已取消";


    String ORDERS_TIPS="您已下单，正在等待对方接单。对方接单前请勿付款，为保证交易顺利安全进行，卖家可能需要您提供银行流水等资料，您可与对方沟通一致后提供";
    String ORDERS_TIPS_2="对方已下单，请及时接单";
    String ORDERS ="对方已接单，请及时支付";
    String REFUSE_ORDER="接单已被对方拒绝。如有疑问，请联系客服";
}

