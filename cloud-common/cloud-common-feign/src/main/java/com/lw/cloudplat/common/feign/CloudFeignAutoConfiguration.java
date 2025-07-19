package com.lw.cloudplat.common.feign;

import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import com.lw.cloudplat.common.feign.interceptor.CustomFeignInnerRequestInterceptor;
import com.lw.cloudplat.common.feign.interceptor.CustomFeignRequestCloseInterceptor;
import com.lw.cloudplat.common.feign.register.CloudFeignClientsRegistrar;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author lw
 * @create 2025-07-19-13:58
 */
@Configuration(proxyBeanMethods = false)
@Import(CloudFeignClientsRegistrar.class)
@AutoConfigureBefore(SentinelFeignAutoConfiguration.class)
public class CloudFeignAutoConfiguration {

    @Bean
    public CustomFeignInnerRequestInterceptor customFeignInnerRequestInterceptor() {
        return new CustomFeignInnerRequestInterceptor();
    }

    @Bean
    public CustomFeignRequestCloseInterceptor customFeignRequestCloseInterceptor() {
        return new CustomFeignRequestCloseInterceptor();
    }
}
