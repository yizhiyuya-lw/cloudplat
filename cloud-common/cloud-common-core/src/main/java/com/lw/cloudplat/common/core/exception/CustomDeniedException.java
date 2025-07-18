package com.lw.cloudplat.common.core.exception;

import lombok.NoArgsConstructor;

/**
 * @author lw
 * @create 2025-07-18-23:28
 */
@NoArgsConstructor
public class CustomDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustomDeniedException(String message) {
        super(message);
    }

    public CustomDeniedException(Throwable cause) {
        super(cause);
    }

    public CustomDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
