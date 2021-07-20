package com.ezcoins.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;


/**
 * @Author:
 * @Email:
 * @Description: validation验证
 * @Date:2021/1/28 11:16
 * @Version:1.0
 */
@Configuration
public class ValidationConfig {
    @Bean
    public Validator getValidator() {
//        Validator validator = Validation.byDefaultProvider().
//                configure().
//                messageInterpolator(new ResourceBundleMessageInterpolator(new PlatformResourceBundleLocator("i18n/messages"))).
//                buildValidatorFactory().getValidator();
//        return validator;
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }
}
