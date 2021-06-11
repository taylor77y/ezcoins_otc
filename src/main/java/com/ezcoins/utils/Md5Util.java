package com.ezcoins.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

/**
 * @Description:
 * @Author:  王磊
 * @Date: 2021/6/1 18:30
 **/
@Slf4j
public class Md5Util{
    /**
     * 盐值
     */
    private  static  final String SALT="taskezcoins";

    public final static String MD5(String s) {
        char hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] btInput = (s+SALT).getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexChars[byte0 >>> 4 & 0xf];
                str[k++] = hexChars[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("MD5 Error...", e);
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(MD5("wanglei"));
    }


}
