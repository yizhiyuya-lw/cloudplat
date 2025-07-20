package com.lw.cloudplat.common.log;

import com.lw.cloudplat.admin.api.feign.RemoteLogService;
import com.lw.cloudplat.common.log.aspect.SysLogAspect;
import com.lw.cloudplat.common.log.config.CustomLogProperties;
import com.lw.cloudplat.common.log.event.SysLogListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author lw
 * @create 2025-07-19-9:13
 */
@EnableAsync
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CustomLogProperties.class)
@ConditionalOnProperty(value = "security.log.enabled", matchIfMissing = true)
public class LogAutoConfiguration {

    @Bean
    public SysLogListener sysLogListener(CustomLogProperties logProperties, RemoteLogService remoteLogService) {
        return new SysLogListener(remoteLogService, logProperties);
    }

    @Bean
    public SysLogAspect sysLogAspect() {
        return new SysLogAspect();
    }
}
