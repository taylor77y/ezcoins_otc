package com.ezcoins.security.configuration;

import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/4 15:13
 * @Version:1.0
 */
@Data
public class TokenProperties {
    private  String header;
    private  String secret;
    private  int expireTime;
}
