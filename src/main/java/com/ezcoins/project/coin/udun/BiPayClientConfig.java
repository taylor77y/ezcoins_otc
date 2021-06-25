package com.ezcoins.project.coin.udun;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BiPayClientConfig {

    @Bean
    @ConfigurationProperties(prefix = "bipay")
    public BiPayClient setBiPayClient(){
        BiPayClient client = new BiPayClient();
        return client;
    }
}
