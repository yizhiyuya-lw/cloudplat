package com.lw.cloudplat.common.feign.sentinel.ext;

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.lw.cloudplat.common.core.util.R;
import feign.Feign;
import feign.InvocationHandlerFactory;
import feign.MethodMetadata;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static feign.Util.checkNotNull;

/**
 * 自动降级Sentinel调用处理器 重写 SentinelInvocationHandler
 * 用于处理默认的返回值为 R 的接口
 * @author lw
 * @create 2025-07-19-15:10
 */
@Slf4j
public class CloudSentinelInvocationHandler implements InvocationHandler {

    /**
     * 目标对象
     */
    private final Target<?> target;

    private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;

    private FallbackFactory fallbackFactory;

    private Map<Method, Method> fallbackMethodMap;

    CloudSentinelInvocationHandler(Target<?> target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch,
                              FallbackFactory fallbackFactory) {
        this.target = checkNotNull(target, "target");
        this.dispatch = checkNotNull(dispatch, "dispatch");
        this.fallbackFactory = fallbackFactory;
        this.fallbackMethodMap = toFallbackMethod(dispatch);
    }

    CloudSentinelInvocationHandler(Target<?> target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
        this.target = checkNotNull(target, "target");
        this.dispatch = checkNotNull(dispatch, "dispatch");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler = args.length > 0 && args[0] != null
                        ? Proxy.getInvocationHandler(args[0])
                        : null;
                return equals(otherHandler);
            }
            catch (IllegalArgumentException e) {
                return false;
            }
        }
        else if ("hashCode".equals(method.getName())) {
            return hashCode();
        }
        else if ("toString".equals(method.getName())) {
            return toString();
        }

        Object result;
        InvocationHandlerFactory.MethodHandler methodHandler = this.dispatch.get(method);
        // only handle by HardCodedTarget
        if (target instanceof Target.HardCodedTarget<?> hardCodedTarget) {
            MethodMetadata methodMetadata = SentinelContractHolder.METADATA_MAP
                    .get(hardCodedTarget.type().getName()
                            + Feign.configKey(hardCodedTarget.type(), method));
            if (Objects.isNull(methodMetadata)) {
                result = methodHandler.invoke(args);
            } else {
                String resourceName = methodMetadata.template().method().toUpperCase()
                        + ":" + hardCodedTarget.url() + methodMetadata.template().path();
                Entry entry = null;
                try {
                    ContextUtil.enter(resourceName);
                    entry = SphU.entry(resourceName, EntryType.OUT, 1, args);
                    result = methodHandler.invoke(args);
                } catch (Throwable ex) {
                    if (!BlockException.isBlockException(ex)) {
                        Tracer.trace(ex);
                    }
                    if (fallbackFactory != null) {
                        try {
                            return fallbackMethodMap.get(method).invoke(fallbackFactory.create(ex), args);
                        }
                        catch (IllegalAccessException e) {
                            // shouldn't happen as method is public due to being an
                            // interface
                            throw new AssertionError(e);
                        }
                        catch (InvocationTargetException e) {
                            throw new AssertionError(e.getCause());
                        }
                    } else {
                        // 若是 R 类型，执行自动降级返回R
                        if (R.class == method.getReturnType()) {
                            log.error("feign 服务间调用异常", ex);
                            return R.failed(ex.getLocalizedMessage());
                        } else {
                            throw ex;
                        }
                    }

                } finally {
                    if (entry != null) {
                        entry.exit(1, args);
                    }
                    ContextUtil.exit();
                }
            }
        } else {
            result = methodHandler.invoke(args);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CloudSentinelInvocationHandler cloudSentinelInvocationHandler) {
            return target.equals(cloudSentinelInvocationHandler.target);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public String toString() {
        return target.toString();
    }

    static Map<Method, Method> toFallbackMethod(Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
        Map<Method, Method> result = new LinkedHashMap<>();
        for (Method method : dispatch.keySet()) {
            method.setAccessible(true);
            result.put(method, method);
        }
        return result;
    }
}
