package com.lw.cloudplat.common.log.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.HttpUtil;
import com.lw.cloudplat.common.core.util.SpringContextHolder;
import com.lw.cloudplat.common.log.config.CustomLogProperties;
import com.lw.cloudplat.common.log.event.SysLogEventSource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 系统日志工具类
 * @author lw
 * @create 2025-07-20-9:30
 */
@UtilityClass
public class SysLogUtils {

    public SysLogEventSource getSysLog() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
                .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        SysLogEventSource sysLog = new SysLogEventSource();
        sysLog.setLogType(LogTypeEnum.NORMAL.getType());
        sysLog.setRequestUri(URLUtil.getPath(request.getRequestURI()));
        sysLog.setMethod(request.getMethod());
        sysLog.setRemoteAddr(JakartaServletUtil.getClientIP(request));
        sysLog.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        sysLog.setCreateBy(getUsername());
        sysLog.setServiceId(SpringContextHolder.getEnvironment().getProperty("spring.application.name"));

        // 敏感数据剔除
        CustomLogProperties logProperties = SpringContextHolder.getBean(CustomLogProperties.class);
        Map<String, String[]> paramsMap = MapUtil.removeAny(new HashMap<>(request.getParameterMap()),
                ArrayUtil.toArray(logProperties.getExcludeFields(), String.class));
        sysLog.setParams(HttpUtil.toParams(paramsMap));
        return sysLog;
    }

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return null;
        }
        return authentication.getName();
    }

    public <T> T getValue(EvaluationContext context, String key, Class<T> clazz) {
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        Expression expression = spelExpressionParser.parseExpression(key);
        return expression.getValue(context, clazz);
    }

    public EvaluationContext getContext(Object[] arguments, Method signatureMethod) {
        String[] parameterNames =
                new StandardReflectionParameterNameDiscoverer().getParameterNames(signatureMethod);
        EvaluationContext context = new StandardEvaluationContext();
        if (Objects.isNull(parameterNames)) {
            return context;
        }
        for (int i = 0; i < arguments.length; i++) {
            context.setVariable(parameterNames[i], arguments[i]);
        }
        return context;
    }
}
