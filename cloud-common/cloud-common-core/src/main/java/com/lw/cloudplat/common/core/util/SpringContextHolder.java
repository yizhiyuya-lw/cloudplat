package com.lw.cloudplat.common.core.util;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * @author lw
 * @create 2025-07-18-23:05
 */
@Slf4j
@Component
@Lazy(false)
public class SpringContextHolder implements ApplicationContextAware, EnvironmentAware, DisposableBean {

    @Getter
    private static ApplicationContext applicationContext = null;

    @Getter
    private static Environment environment = null;

    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static void publishEvent(ApplicationEvent event) {
        if (Objects.nonNull(applicationContext) && Objects.nonNull(event)) {
            applicationContext.publishEvent(event);
        }
    }

    public static boolean isMicro() {
        return environment.getProperty("spring.cloud.nacos.discovery.enabled", Boolean.class, true);
    }

    public static void clearHolder() {
        if (log.isDebugEnabled()) {
            log.debug("清除SpringContextHolder中的ApplicationContext: {}", applicationContext);
        }
        applicationContext = null;
    }

    @Override
    @SneakyThrows
    public void destroy() {
        SpringContextHolder.clearHolder();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        SpringContextHolder.environment = environment;
    }
}
