package com.lw.cloudplat.common.feign.annotation;

import java.lang.annotation.*;

/**
 * 服务无token调用声明注解
 * @author lw
 * @create 2025-07-19-14:09
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoToken {
}
