package com.lw.cloudplat.common.mybatis.resolver;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Mybatis Plus Order By SQL注入问题解决方案
 * @author lw
 * @create 2025-07-19-10:49
 */
public class SqlFilterArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 判断Controller方法参数是否为Page类型
     * @param parameter the method parameter to check
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Page.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String[] ascs = request.getParameterValues("ascs");
        String[] descs = request.getParameterValues("descs");
        String current = request.getParameter("current");
        String size = request.getParameter("size");
        Page<?> page = new Page<>();
        if (StrUtil.isNotBlank(current)) {
            page.setCurrent(Long.parseLong(current));
        }

        if (StrUtil.isNotBlank(size)) {
            page.setSize(Long.parseLong(size));
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        Optional.ofNullable(ascs)
                .ifPresent(sArr ->
                        orderItemList.addAll(Arrays.stream(ascs)
                                                    .filter(asc -> !SqlInjectionUtils.check(asc))
                                                    .map(OrderItem::asc).toList()));
        Optional.ofNullable(descs)
                .ifPresent(sArr ->
                        orderItemList.addAll(Arrays.stream(descs)
                                                    .filter(desc -> !SqlInjectionUtils.check(desc))
                                                    .map(OrderItem::desc).toList()));
        page.addOrder(orderItemList);
        return page;
    }
}
