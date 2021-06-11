package com.ezcoins.security.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/4 15:13
 * @Version:1.0
 */
@Configuration
public class TokenPropertiesConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "token")
    public TokenProperties tokenProperties() {
        return new TokenProperties();
    }
}
