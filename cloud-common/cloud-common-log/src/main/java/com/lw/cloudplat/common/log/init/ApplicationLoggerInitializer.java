package com.lw.cloudplat.common.log.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 应用日志初始化类
 * @author lw
 * @create 2025-07-20-10:10
 */
public class ApplicationLoggerInitializer implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String applicationName = environment.getProperty("spring.application.name");
        String logBase = environment.getProperty("LOGGING_PATH", "logs");

        // spring boot admin 直接加载日志
        System.setProperty("logging.file.name", String.format("%s/%s/debug.log", logBase, applicationName));

        // 避免各种依赖的地方组件造成 BeanPostProcessorChecker 警告
        System.setProperty("logging.level.org.springframework.context.support.PostProcessorRegistrationDelegate",
                "ERROR");

        // 避免 sentinel 1.8.4+ 心跳日志过大
        System.setProperty("csp.sentinel.log.level", "OFF");

        // 避免 sentinel 健康检查 server
        System.setProperty("management.health.sentinel.enabled", "false");
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
