package com.ezcoins.utils;


/**
 * @Author:
 * @Email:
 * @Description: 获取默认值
 * @Date:2021/5/26 18:03
 * @Version:1.0
 */
public final class DefaultUtils {

    private DefaultUtils() {
    }

    ;

    /**
     * 获取默认字符串
     */
    public static String getDefaultString(String dest, String def) {
        if (StringUtils.isNotEmpty(dest)) {
            return dest;
        } else {
            if (StringUtils.isNotEmpty(def)) {
                return def;
            }
            return "";
        }
    }

    /**
     * 获取默认对象
     */
    public static <T> T getDefault(T t, T def) {
        if (StringUtils.isNotNull(t)) {
            return t;
        }
        return def;
    }
}
