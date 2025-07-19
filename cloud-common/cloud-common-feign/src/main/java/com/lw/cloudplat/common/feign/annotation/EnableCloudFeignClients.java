package com.lw.cloudplat.common.feign.annotation;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 启用Feign 客户端注解
 * @author lw
 * @create 2025-07-19-14:06
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients
public @interface EnableCloudFeignClients {

    /**
     * basePackages() 属性别名
     * @return
     */
    String[] value() default {};

    @AliasFor(annotation = EnableFeignClients.class, attribute = "basePackages")
    String[] basePackages() default { "com.lw.cloudplat" };
}
