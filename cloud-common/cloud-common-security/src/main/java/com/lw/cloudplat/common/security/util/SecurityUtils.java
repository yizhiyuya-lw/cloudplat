package com.lw.cloudplat.common.security.util;

import com.lw.cloudplat.common.security.service.CloudUser;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;

/**
 * @author lw
 * @create 2025-07-19-22:38
 */
@UtilityClass
public class SecurityUtils {

    /**
     * 获取当前认证用户
     * @return 当前认证用户对象，未认证时返回null
     */
    public CloudUser getUser() {
        // todo
        return null;
    }
}
