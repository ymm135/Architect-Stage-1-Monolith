package com.imooc.plugin;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class MyBatisPlugin implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("本次库操作intercept");

        //通过StatementHandler获取执行的sql
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();

        long start = System.currentTimeMillis();
        Object proceed = invocation.proceed();
        long end = System.currentTimeMillis();

        System.out.println("本次数据库操作是慢查询，sql是:" + sql);
        return proceed;
    }

    //获取到拦截的对象，底层也是通过代理实现的，实际上是拿到一个目标代理对象
    @Override
    public Object plugin(Object target) {
        System.out.println("本次操作 plugin=" + target);
        return Plugin.wrap(target, this);
    }

    //获取设置的阈值等参数
    @Override
    public void setProperties(Properties properties) {
        System.out.println("本次操作 setProperties");
    }
}
