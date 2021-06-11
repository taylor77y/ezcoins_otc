package com.ezcoins.context;


import com.ezcoins.constant.interf.JWTConstans;
import com.ezcoins.security.util.StringHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/4 11:51
 * @Version:1.0
 */
public class ContextHandler {
    public static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>();

    public static void set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<String, Object>();
            threadLocal.set(map);
        }
        map.put(key, value);
    }

    public static Object get(String key){
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<String, Object>();
            threadLocal.set(map);
        }
        return map.get(key);
    }

    public static String getUserId(){
        Object value = get(JWTConstans.CONTEXT_KEY_USER_ID);
        return returnObjectValue(value);
    }

    public static String getUserName(){
        Object value = get(JWTConstans.CONTEXT_KEY_USERNAME);
        return returnObjectValue(value);
    }


    public static String getUserType(){
        Object value = get(JWTConstans.CONTEXT_KEY_USERTYPE);
        return StringHelper.getObjectValue(value);
    }

    public static String getToken(){
        Object value = get(JWTConstans.CONTEXT_KEY_USER_TOKEN);
        return StringHelper.getObjectValue(value);
    }
    public static void setToken(String token){set(JWTConstans.CONTEXT_KEY_USER_TOKEN,token);}

    public static void setUserType(String userType){set(JWTConstans.CONTEXT_KEY_USERTYPE,userType);}

    public static void setUserId(String userId){
        set(JWTConstans.CONTEXT_KEY_USER_ID,userId);
    }

    public static void setUserName(String username){
        set(JWTConstans.CONTEXT_KEY_USERNAME,username);
    }

    private static String returnObjectValue(Object value) {
        return value==null?null:value.toString();
    }

    public static void remove(){
        threadLocal.remove();
    }
}
