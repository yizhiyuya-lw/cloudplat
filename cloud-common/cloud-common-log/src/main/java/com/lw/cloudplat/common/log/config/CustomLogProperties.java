package com.lw.cloudplat.common.log.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author lw
 * @create 2025-07-19-9:17
 */
@Getter
@Setter
@ConfigurationProperties(prefix = CustomLogProperties.PREFIX)
public class CustomLogProperties {

    public static final String PREFIX = "security.log";

    private boolean enable = true;

    @Value("${security.log.exclude-fields:password,mobile,idcard,phone}")
    private List<String> excludeFields;

    private Integer maxLength = 2000;
}
