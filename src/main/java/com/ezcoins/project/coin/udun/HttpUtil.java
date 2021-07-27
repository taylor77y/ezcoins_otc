package com.ezcoins.project.coin.udun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * sign
 * <p>
 * key
 * <p>
 * body
 * <p>
 * body{
 * amount..
 * <p>
 * }
 * timestamp
 * <p>
 * sign
 * map add timestamp
 * <p>
 * key aes
 * <p>
 * map md5   sign
 */

@Slf4j
public class HttpUtil {

    public static Map<String, String> wrapperParams(String key, String body) throws Exception {
        String timestamp = System.currentTimeMillis() + "";
        String nonce = String.valueOf(getNonceString(8));
        String sign = SignUtil.sign(key, timestamp, nonce, body);
        Map<String, String> map = new HashMap<>();
        map.put("body", body);
        map.put("sign", sign);//设置签名
        map.put("timestamp", timestamp);//设置时间戳
        map.put("nonce", nonce);//设置随机数
        return map;
    }

    public static String getNonceString(int len) {
        String seed = "1234567890";
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < len; i++) {
            tmp.append(seed.charAt(getRandomNumber(0, 9)));
        }
        return tmp.toString();
    }

    public static int getRandomNumber(int from, int to) {
        float a = from + (to - from) * (new Random().nextFloat());
        int b = (int) a;
        return ((a - b) > 0.5 ? 1 : 0) + b;
    }


    public static boolean checkSign(String sign,String timestamp,String body,String encrypted) throws Exception {
      if (!"47023067b4ac0b6d3cab1f667acc81c7".equals(sign)){
          return false;
      }
      String checkSign = SignUtil.sign(sign, timestamp,body);
      return checkSign.equals(encrypted);
    }


    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a", "1");
        jsonObject.put("b", "2");
        System.out.println(JSON.toJSONString(jsonObject));

        //{"a":"1","c":"2","b":"5"}

        //{"a":"1","b":"2","c":"5"}


        // sing   body   time


        // body+time

        // md5()   sign


        //body {"a":"1"}

    }

}
