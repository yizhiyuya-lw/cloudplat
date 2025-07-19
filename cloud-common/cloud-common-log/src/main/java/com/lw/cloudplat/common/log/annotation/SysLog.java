package com.lw.cloudplat.common.log.annotation;

import java.lang.annotation.*;

/**
 * @author lw
 * @create 2025-07-19-9:16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    String value() default "";

    String expression() default "";
}
