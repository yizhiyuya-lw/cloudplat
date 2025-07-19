package com.lw.cloudplat.common.feign.sentinel;

import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.RequestOriginParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lw.cloudplat.common.feign.sentinel.ext.CloudSentinelFeign;
import com.lw.cloudplat.common.feign.sentinel.handle.CloudUrlBlockHandler;
import com.lw.cloudplat.common.feign.sentinel.parser.CloudHeaderRequestOriginParser;
import feign.Feign;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Sentinel 自动配置类
 * @author lw
 * @create 2025-07-19-15:07
 */
@Configuration
@AutoConfigureBefore(SentinelFeignAutoConfiguration.class)
public class SentinelAutoConfiguration {

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "spring.cloud.openfeign.sentinel.enabled")
    public Feign.Builder feignSentinelBuilder() {
        return CloudSentinelFeign.builder();
    }

    @Bean
    @ConditionalOnMissingBean
    public BlockExceptionHandler blockExceptionHandler(ObjectMapper objectMapper) {
        return new CloudUrlBlockHandler(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestOriginParser requestOriginParser() {
        return new CloudHeaderRequestOriginParser();
    }
}
