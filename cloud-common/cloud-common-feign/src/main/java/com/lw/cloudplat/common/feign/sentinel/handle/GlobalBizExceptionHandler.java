package com.lw.cloudplat.common.feign.sentinel.handle;

import com.alibaba.csp.sentinel.Tracer;
import com.lw.cloudplat.common.core.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * 全局业务异常处理器，结合Sentinel处理系统异常
 * 全局异常处理器不能作用在Oauth Server
 * @author lw
 * @create 2025-07-19-15:38
 */
@Slf4j
@Order(10000)
@RestControllerAdvice
@ConditionalOnExpression("!'${security.oauth2.client.clientId}'.isEmpty()")
public class GlobalBizExceptionHandler {

    /**
     * 全局异常.
     * @param e the e
     * @return R
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R handleGlobalException(Exception e) {
        log.error("全局异常信息 ex={}", e.getMessage(), e);

        // 业务异常交由 sentinel 记录
        Tracer.trace(e);
        return R.failed(e.getLocalizedMessage());
    }

    /**
     * 处理业务校验过程中碰到的非法参数异常 该异常基本由{@link org.springframework.util.Assert}抛出
     * @param exception 参数校验异常
     * @return API返回结果对象包装后的错误输出结果
     * @see Assert#hasLength(String, String)
     * @see Assert#hasText(String, String)
     * @see Assert#isTrue(boolean, String)
     * @see Assert#isNull(Object, String)
     * @see Assert#notNull(Object, String)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public R handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("非法参数,ex = {}", exception.getMessage(), exception);
        return R.failed(exception.getMessage());
    }

    /**
     * AccessDeniedException
     * @param e the e
     * @return R
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R handleAccessDeniedException(AccessDeniedException e) {
        String msg = SpringSecurityMessageSource.getAccessor()
                .getMessage("AbstractAccessDecisionManager.accessDenied", e.getMessage());
        log.warn("拒绝授权异常信息 ex={}", msg);
        return R.failed(msg);
    }

    /**
     * validation Exception
     * @param exception
     * @return R
     */
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleBodyValidException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        log.warn("参数绑定异常,ex = {}", fieldErrors.get(0).getDefaultMessage());
        return R.failed(String.format("%s %s", fieldErrors.get(0).getField(), fieldErrors.get(0).getDefaultMessage()));
    }

    /**
     * validation Exception (以form-data形式传参)
     * @param exception
     * @return R
     */
    @ExceptionHandler({ BindException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R bindExceptionHandler(BindException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        log.warn("参数绑定异常,ex = {}", fieldErrors.get(0).getDefaultMessage());
        return R.failed(fieldErrors.get(0).getDefaultMessage());
    }

    /**
     * 保持和低版本请求路径不存在的行为一致
     * <p>
     * <a href="https://github.com/spring-projects/spring-boot/issues/38733">[Spring Boot
     * 3.2.0] 404 Not Found behavior #38733</a>
     * @param exception
     * @return R
     */
    @ExceptionHandler({ NoResourceFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R notFoundExceptionHandler(NoResourceFoundException exception) {
        log.debug("请求路径 404 {}", exception.getMessage());
        return R.failed(exception.getMessage());
    }
}
