/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.metrics.Event;
import com.ciicgat.grus.metrics.ModuleEventType;
import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class,
                        CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
}
)
public class SQLTelemetryInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLTelemetryInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] args = invocation.getArgs();
        final MappedStatement mappedStatement = (MappedStatement) args[0];

        String fullMethodString = mappedStatement.getId();
        String className = "unknown";
        String methodName = fullMethodString;

        String[] splits = methodName.split("\\.");
        if (splits.length >= 2) {
            methodName = splits[splits.length - 1];
            className = splits[splits.length - 2];
        }
        String[] tags = new String[]{"method", methodName, "class", className};

        Event event = ModuleEventType.DB_REQUEST.newEvent(fullMethodString, tags, fullMethodString);
        try {
            Object result = invocation.proceed();
            if (result instanceof List && ((List) result).size() > 40960L) {
                String msg = String.format("too large query result size:[%d] ,sql is %s", ((List) result).size(), fullMethodString);
                LOGGER.warn(msg);
                FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT, Module.DB, msg, null);
            }
            return result;

        } catch (InvocationTargetException e) {
            LOGGER.error(fullMethodString, e);
            event.error(fullMethodString, e.getCause());
            throw e;

        } catch (Throwable throwable) {
            LOGGER.error(fullMethodString, throwable);
            event.error(fullMethodString, throwable);
            throw throwable;
        } finally {
            CloseUtils.close(event);
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
