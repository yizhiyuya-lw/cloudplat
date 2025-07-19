package com.lw.cloudplat.common.mybatis.plugins;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Objects;

/**
 * 自定义分页拦截器
 * @author lw
 * @create 2025-07-19-10:33
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CustomPaginationInnerInterceptor extends PaginationInnerInterceptor {

    /**
     * 数据库类型
     */
    private DbType dbType;

    /**
     * 方言实现类
     */
    private IDialect dialect;

    public CustomPaginationInnerInterceptor(DbType dbType) {
        this.dbType = dbType;
    }

    public CustomPaginationInnerInterceptor(IDialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {

        IPage<?> page = ParameterUtils.findPage(parameter).orElse(null);

        if (Objects.nonNull(page) && page.getSize() < 0) {
            page.setSize(0);
        }

        super.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
    }
}
