package com.lw.cloudplat.common.mybatis.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lw.cloudplat.common.core.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ClassUtils;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * MybatisPlus 自动填充处理器，用于实体类字段的自动填充
 * @author lw
 * @create 2025-07-19-10:01
 */
@Slf4j
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("mybatis plus start insert fill ...");
        LocalDateTime now = LocalDateTime.now();

        fillValIfNullByName("createTime", now, metaObject, true);
        fillValIfNullByName("updateTime", now, metaObject, true);
        fillValIfNullByName("createBy", getUserName(), metaObject, true);
        fillValIfNullByName("updateBy", getUserName(), metaObject, true);

        // 删除标记自动填充
        fillValIfNullByName("delFlag", CommonConstants.STATUS_NORMAL, metaObject, true);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("mybatis plus start update fill ...");
        fillValIfNullByName("updateTime", LocalDateTime.now(), metaObject, true);
        fillValIfNullByName("updateBy", getUserName(), metaObject, true);
    }

    /**
     * 填充值，先判断是否有手动设置，优先手动设置的值
     * @param fieldName 属性名
     * @param fieldVal 属性值
     * @param metaobject MetaObject
     * @param isCover 是否覆盖原有值，避免更新操作手动入参
     */
    private static void fillValIfNullByName(String fieldName, Object fieldVal, MetaObject metaobject, boolean isCover) {
        if (Objects.isNull(fieldVal)) {
            return;
        }

        // 1. 没有 set 方法
        if (!metaobject.hasSetter(fieldName)) {
            return ;
        }
        // 2. 如果用户有手动设置的值
        Object userSetValue = metaobject.getValue(fieldName);
        String setValueStr = StrUtil.str(userSetValue, Charset.defaultCharset());
        if (StrUtil.isNotBlank(setValueStr) && !isCover) {
            return;
        }
        // 3. field 类型相同时设置
        Class<?> getterType = metaobject.getGetterType(fieldName);
        if (ClassUtils.isAssignableValue(getterType, fieldVal)) {
            metaobject.setValue(fieldName, fieldVal);
        }
    }

    /**
     * 获取 spring security 当前登陆的用户名
     * @return
     */
    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 匿名接口直接返回
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        if (Optional.ofNullable(authentication).isPresent()) {
            return authentication.getName();
        }
        return null;
    }
}
