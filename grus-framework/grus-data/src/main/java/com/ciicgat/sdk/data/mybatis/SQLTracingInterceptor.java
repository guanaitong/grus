/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;
import java.util.List;
import java.util.Properties;

/**
 * Created by August.Zhou on 2019-06-25 16:00.
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})
})
public class SQLTracingInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLTracingInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String sql = "";
        if (invocation.getTarget() instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = statementHandler.getBoundSql();
            sql = boundSql.getSql();
        }
        Tracer tracer = OpenTelemetrys.get();
        Span span = tracer.spanBuilder("executeSQL").setSpanKind(SpanKind.CLIENT).setParent(Context.current()).startSpan();

        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            span.setAttribute("db.type", "mysql");
            span.setAttribute("component", "mysql");
            span.setAttribute("db.statement", sql);

            Object result = invocation.proceed();
            if (result instanceof List && ((List) result).size() > 10000) {
                String msg = String.format("too large query result size:[%d] ,sql is %s", ((List) result).size(), sql);
                LOGGER.warn(msg);
                Alert.send(msg);
            }
            return result;
        } catch (Throwable throwable) {
            LOGGER.error(sql, throwable);
            throw throwable;
        } finally {
            span.end();
            SlowLogger.logEvent(Module.DB, span, sql);
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }


}
