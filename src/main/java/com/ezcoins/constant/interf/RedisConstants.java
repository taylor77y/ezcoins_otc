package com.ezcoins.constant.interf;

/**
 * @Author:
 * @Email:
 * @Description:
 * @Date:2021/6/3 15:49
 * @Version:1.0
 */
public interface RedisConstants {
    /**
     * 登录角色权限
     */
    public static final String LOGIN_USER_KEY = "login_user_key:";


    /**
     * 管理员权限
     */
    public static final String LOGIN_MENU_CODE = "login_menu_code:";

    /**
     * 短信配置
     */
    public static final String SMS_CONFIG_KEY = "sms_config_key:";

    /**
     * 取消订单次数 key
     */
    public static final String CANCEL_ORDER_COUNT_KEY = "cancel_order_count_key:";








    /**
     * Verification code
     */
    public static final String PHONE_REGISTER_SMS_KEY = "phone_register_sms_key:";//注册发送手机验证
    public static final String EMAIL_REGISTER_SMS_KEY = "email_register_sms_key:";//注册发送邮箱验证

    public static final String PHONE_RETRIEVE_PASSWORD_SMS_KEY = "phone_retrieve_password_sms_key:";//找回发送手机验证
    public static final String EMAIL_RETRIEVE_PASSWORD_SMS_KEY = "email_retrieve_password_sms_key:";//找回发送邮箱验证








}
