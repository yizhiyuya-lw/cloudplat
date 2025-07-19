package com.lw.cloudplat.common.feign.sentinel.parser;

import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.RequestOriginParser;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Sentinel 请求头解析判断实现类，用于从HTTP请求头中获取Allow字段值
 * @author lw
 * @create 2025-07-19-15:36
 */
public class CloudHeaderRequestOriginParser implements RequestOriginParser {

    private static final String ALLOW = "Allow";

    @Override
    public String parseOrigin(HttpServletRequest request) {
        return request.getHeader(ALLOW);
    }
}
