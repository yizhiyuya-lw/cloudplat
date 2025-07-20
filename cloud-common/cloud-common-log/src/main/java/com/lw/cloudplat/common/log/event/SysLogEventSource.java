package com.lw.cloudplat.common.log.event;

import com.lw.cloudplat.admin.api.entity.SysLog;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lw
 * @create 2025-07-20-9:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysLogEventSource extends SysLog {

    /**
     * 参数重写成object
     */
    private Object body;
}
