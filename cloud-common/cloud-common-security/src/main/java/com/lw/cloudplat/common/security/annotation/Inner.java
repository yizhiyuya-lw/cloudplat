package com.lw.cloudplat.common.security.annotation;

import java.lang.annotation.*;

/**
 * @author lw
 * @create 2025-07-19-22:44
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inner {

    /**
     * 是否AOP统一处理
     * @return false, true
     */
    boolean value() default true;

    /**
     * 需要特殊判空的字段(预留)
     * @return {}
     */
    String[] field() default {};

}
