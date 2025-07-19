package com.lw.cloudplat.common.feign.interceptor;

import com.lw.cloudplat.common.core.constant.SecurityConstants;
import com.lw.cloudplat.common.feign.annotation.NoToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * Feign 内部请求拦截器，用于处理Feign 请求的 Token校验
 * @author lw
 * @create 2025-07-19-14:17
 */
public class CustomFeignInnerRequestInterceptor implements RequestInterceptor, Ordered {
    @Override
    public void apply(RequestTemplate template) {
        Method method = template.methodMetadata().method();
        NoToken noToken = method.getAnnotation(NoToken.class);
        if (noToken != null) {
            template.header(SecurityConstants.FROM, SecurityConstants.FROM_IN);
        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
