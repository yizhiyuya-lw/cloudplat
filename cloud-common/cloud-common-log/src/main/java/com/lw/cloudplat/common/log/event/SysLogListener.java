package com.lw.cloudplat.common.log.event;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.lw.cloudplat.admin.api.entity.SysLog;
import com.lw.cloudplat.admin.api.feign.RemoteLogService;
import com.lw.cloudplat.common.core.jackson.CustomJavaTimeModule;
import com.lw.cloudplat.common.log.config.CustomLogProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.Objects;

/**
 * @author lw
 * @create 2025-07-20-9:59
 */
@Slf4j
@RequiredArgsConstructor
public class SysLogListener implements InitializingBean {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final RemoteLogService remoteLogService;

    private final CustomLogProperties logProperties;

    @SneakyThrows
    @Async
    @Order
    @EventListener(SysLogEvent.class)
    public void saveSysLog(SysLogEvent event) {
        SysLogEventSource source = (SysLogEventSource) event.getSource();
        SysLog sysLog = new SysLog();
        BeanUtils.copyProperties(source, sysLog);
        if (Objects.nonNull(source.getBody())) {
            String params = objectMapper.writeValueAsString(source.getBody());
            sysLog.setParams(StrUtil.subPre(params, logProperties.getMaxLength()));
        }
        remoteLogService.saveLog(sysLog);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        objectMapper.addMixIn(Object.class, PropertyFilterMixIn.class);
        String[] ignorableFieldNames = logProperties.getExcludeFields().toArray(new String[0]);
        SimpleFilterProvider filters = new SimpleFilterProvider().addFilter("filter properties by name",
                SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames));
        objectMapper.setFilterProvider(filters);
        objectMapper.registerModule(new CustomJavaTimeModule());
    }

    @JsonFilter("filter properties by name")
    class PropertyFilterMixIn {

    }
}
