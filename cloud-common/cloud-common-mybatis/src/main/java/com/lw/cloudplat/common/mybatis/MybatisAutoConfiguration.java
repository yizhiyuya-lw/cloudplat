package com.lw.cloudplat.common.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.lw.cloudplat.common.mybatis.config.MybatisPlusMetaObjectHandler;
import com.lw.cloudplat.common.mybatis.plugins.CustomPaginationInnerInterceptor;
import com.lw.cloudplat.common.mybatis.resolver.SqlFilterArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author lw
 * @create 2025-07-19-9:49
 */
@Configuration(proxyBeanMethods = false)
public class MybatisAutoConfiguration implements WebMvcConfigurer {

    /**
     * 添加SQL过滤器参数解析器，避免SQL注入
     * @param resolvers initially an empty list
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SqlFilterArgumentResolver());
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new CustomPaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 字段自动填充处理器
     * @return
     */
    @Bean
    public MybatisPlusMetaObjectHandler mybatisPlusMetaObjectHandler() {
        return new MybatisPlusMetaObjectHandler();
    }
}
