package com.lw.cloudplat.common.log.aspect;

import cn.hutool.core.util.StrUtil;
import com.lw.cloudplat.common.log.annotation.SysLog;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

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

        }
    }
}
