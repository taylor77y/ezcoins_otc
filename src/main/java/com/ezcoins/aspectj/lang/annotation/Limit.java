package com.ezcoins.aspectj.lang.annotation;

import com.ezcoins.constant.enums.BusinessType;
import com.ezcoins.constant.enums.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {
    /**
     * 验证类型
     */
    public LimitType LIMIT_TYPE() default LimitType.LOGINLIMIT;


}
