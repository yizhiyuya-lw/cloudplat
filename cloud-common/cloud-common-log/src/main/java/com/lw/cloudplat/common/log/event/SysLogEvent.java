package com.lw.cloudplat.common.log.event;

import com.lw.cloudplat.admin.api.entity.SysLog;
import org.springframework.context.ApplicationEvent;

/**
 * @author lw
 * @create 2025-07-19-9:45
 */
public class SysLogEvent extends ApplicationEvent {

    public SysLogEvent(SysLog source) {
        super(source);
    }
}
