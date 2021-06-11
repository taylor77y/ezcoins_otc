package com.ezcoins.aspectj.lang.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthToken {
    //是否验证用户激活状态
    boolean active() default false;
    //是否验证用户kyc状态
    boolean kyc() default false;
    //是否验证用户状态
    boolean status() default true;

}