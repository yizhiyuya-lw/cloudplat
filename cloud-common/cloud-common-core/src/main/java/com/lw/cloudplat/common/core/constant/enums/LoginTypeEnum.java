package com.lw.cloudplat.common.core.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author lw
 * @create 2025-07-18-22:39
 */
@Getter
@RequiredArgsConstructor
public enum LoginTypeEnum {

    PWD("PWD", "账号密码登陆"),

    SMS("SMS", "验证码登陆")
    ;

    private final String type;

    private final String description;
}
