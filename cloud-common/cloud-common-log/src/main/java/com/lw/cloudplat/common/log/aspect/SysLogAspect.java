package com.lw.cloudplat.common.log.aspect;

import cn.hutool.core.util.StrUtil;
import com.lw.cloudplat.common.core.util.SpringContextHolder;
import com.lw.cloudplat.common.log.annotation.SysLog;
import com.lw.cloudplat.common.log.event.SysLogEvent;
import com.lw.cloudplat.common.log.event.SysLogEventSource;
import com.lw.cloudplat.common.log.util.LogTypeEnum;
import com.lw.cloudplat.common.log.util.SysLogUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;

/**
 * 系统日志切面类，通过Spring AOP实现操作日志的异步记录
 * @author lw
 * @create 2025-07-19-9:23
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
public class SysLogAspect {

    @Around("@annotation(sysLog)")
    @SneakyThrows
    public Object around(ProceedingJoinPoint point, SysLog sysLog) {
        String strClassName = point.getTarget().getClass().getName();
        String strMethodName = point.getSignature().getName();
        log.debug("[类名]: {}, [方法]: {}", strClassName, strMethodName);

        String value = sysLog.value();
        String expression = sysLog.expression();
        // 当表达式存在SPEL，会覆盖value
        if (StrUtil.isNotBlank(expression)) {
            MethodSignature signature = (MethodSignature) point.getSignature();
            EvaluationContext context = SysLogUtils.getContext(point.getArgs(), signature.getMethod());
            try {
                value = SysLogUtils.getValue(context, expression, String.class);
            } catch (Exception e) {
                log.error("@SysLog 解析SPEL {} 异常", expression, e);
            }
        }
        SysLogEventSource logVo = SysLogUtils.getSysLog();
        logVo.setTitle(value);
        if (StrUtil.isBlank(logVo.getParams())) {
            logVo.setBody(point.getArgs());
        }
        long startTime = System.currentTimeMillis();
        Object obj;
        try {
            obj = point.proceed();
        } catch (Exception e) {
            logVo.setLogType(LogTypeEnum.ERROR.getType());
            logVo.setException(e.getMessage());
            throw e;
        } finally {
            Long endTime = System.currentTimeMillis();
            logVo.setTime(endTime - startTime);
            SpringContextHolder.publishEvent(new SysLogEvent(logVo));
        }
        return obj;
    }
}
