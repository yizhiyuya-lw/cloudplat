package com.lw.cloudplat.common.log.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author lw
 * @create 2025-07-19-9:13
 */
@Getter
@RequiredArgsConstructor
public enum LogTypeEnum {

    NORMAL("0", "正常日志"),
    ERROR("9", "错误日志")
    ;

    private final String type;

    private final String description;
}
