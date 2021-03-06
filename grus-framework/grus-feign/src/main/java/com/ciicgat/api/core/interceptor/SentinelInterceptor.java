/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.interceptor;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import com.ciicgat.sdk.util.system.Systems;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class SentinelInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SentinelInterceptor.class);

    // 被调用的服务名称
    private String serviceName;

    public SentinelInterceptor(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 获取请求
        Request originalRequest = chain.request();
        // 拼装资源
        StringBuilder resourceNameBuilder = new StringBuilder(originalRequest.method()).append(":").append(serviceName);
        String path = originalRequest.url().encodedPath();
        if (Objects.nonNull(path)) {
            resourceNameBuilder.append(path);
        }
        String resourceName = resourceNameBuilder.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("resourceName: {}", resourceName);
        }
        // 获取请求参数，用于热点限流规则，参数需要加@RequestParam注解，不支持@RequestBody注解
        Set<String> parameterSet = originalRequest.url().queryParameterNames();

        // sentinel处理
        Entry entry = null;
        try {
            // 注入调用方appName，需要在SphU.entry方法之前执行才生效，并且需要放到try代码块，故无法使用try resources写法
            ContextUtil.enter(resourceName, Systems.APP_NAME);
            entry = SphU.entry(resourceName, EntryType.OUT, 1, parameterSet);
            return chain.proceed(originalRequest);
        } catch (BlockException e) {
            LOGGER.error("SENTINEL BLOCKED ", e);
            throw new BusinessRuntimeException(ErrorCode.REQUEST_BLOCK);
        } catch (Throwable ex) {
            if (BlockException.isBlockException(ex)) {
                LOGGER.error("SENTINEL BLOCKED WITH MSG ", ex);
                throw new BusinessRuntimeException(ErrorCode.REQUEST_BLOCK);
            }
            LOGGER.error("SENTINEL ERROR TRACE ", ex);
            // 降级跟踪必备代码
            Tracer.trace(ex);
            throw ex;
        } finally {
            // 推出资源
            if (entry != null) {
                entry.exit(1, parameterSet);
            }
            ContextUtil.exit();
        }
    }
}
