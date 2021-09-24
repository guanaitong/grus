/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.sdk.trace.SpanUtil;
import com.ciicgat.sdk.trace.Spans;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopSpan;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Span rootSpan = Spans.getRootSpan();
        Tracer tracer = GlobalTracer.get();

        final Span span = tracer.buildSpan("executeSQL")
                .asChildOf(rootSpan == NoopSpan.INSTANCE ? null : rootSpan)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                .start();

        Spans.setSystemTags(span);
        Tags.DB_TYPE.set(span, "mysql");
        Tags.COMPONENT.set(span, "mysql");
        Tags.DB_STATEMENT.set(span, sql);

        try {
            Object result = invocation.proceed();
            if (result instanceof List && ((List) result).size() > 10000) {
                String msg = String.format("too large query result size:[%d] ,sql is %s", ((List) result).size(), sql);
                LOGGER.warn(msg);
                FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT, Module.DB, msg, null);
            }
            return result;
        } catch (Throwable throwable) {
            LOGGER.error(sql, throwable);
            Tags.ERROR.set(span, Boolean.TRUE);
            span.log(errorLogs(throwable));
            throw throwable;
        } finally {
            span.finish();
            long duration = SpanUtil.getDurationMilliSeconds(span);
            SlowLogger.logEvent(Module.DB, duration, sql);
        }
    }


    protected Map<String, Object> errorLogs(Throwable throwable) {
        Map<String, Object> errorLogs = new HashMap<>(2);
        errorLogs.put("event", Tags.ERROR.getKey());
        errorLogs.put("error.object", throwable);
        return errorLogs;
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
