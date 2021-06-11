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
     * Verification code
     */
    public static final String REGISTER_SMS_KEY = "register_sms_key:";//注册发送手机验证


    public static final String RETRIEVE_PASSWORD_SMS_KEY = "retrieve_password_sms_key:";//找回发送手机验证





}
