package com.lw.cloudplat.common.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;

/**
 * Feign请求连接关闭拦截器
 * 请求头设置： Connection: close 表示当前请求处理完成后立即关闭 TCP 连接
 * @author lw
 * @create 2025-07-19-14:22
 */
public class CustomFeignRequestCloseInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header(HttpHeaders.CONNECTION, "close");
    }
}
