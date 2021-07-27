package com.ezcoins.project.coin.udun;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * 签名工具类
 */
public class SignUtil {

//    public static String sign(String key,String timestamp,String nonce,String type,String body) throws Exception {
//        return DigestUtils.md5Hex(body + key + nonce + timestamp + type).toLowerCase();
//    }

    public static String sign(String key,String timestamp,String none,String body) throws Exception {
        String raw = body + key + timestamp;
        String sign = DigestUtils.md5Hex(raw).toLowerCase();
        return sign;
    }

    public static String sign(String username, String address, String transactionId, String amount, String status) throws Exception {
        String raw = username + address + transactionId + amount + status;
        return DigestUtils.md5Hex(raw);
    }

    public static String sign(String sign,String timestamp,String body) throws Exception {
        return DigestUtils.md5Hex(sign + timestamp + body );
    }


    public static void main(String[] args) {



    }
}
