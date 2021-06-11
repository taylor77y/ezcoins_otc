package com.ezcoins.project.common.configruatin;

import org.springframework.context.annotation.Bean;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/7 19:06
 * @Version:1.0
 */
@Deprecated
public class SmsPropertiesConfiguration {
    @Deprecated
    @Bean
    public SmsProperties smsProperties() {
        return new SmsProperties();
    }
}
