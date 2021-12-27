/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.grus.service.GrusFramework;
import com.ciicgat.grus.service.GrusRuntimeManager;
import com.ciicgat.grus.service.GrusServiceHttpHeader;
import com.ciicgat.grus.service.GrusServiceStatus;
import com.ciicgat.sdk.util.system.Systems;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2019-06-19 13:51.
 */
public class GrusFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrusFilter.class);
    private static final String SERVER_SPAN_CONTEXT = GrusFilter.class.getName() + ".activeSpanContext";
    private static final Object TAG_VALUE = new Object();
    private final AtomicInteger current = new AtomicInteger();

    public GrusFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    static final TextMapGetter<HttpServletRequest> getter = new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(HttpServletRequest httpServletRequest) {
            return () -> httpServletRequest.getHeaderNames().asIterator();
        }

        @Override
        public String get(HttpServletRequest httpServletRequest, String key) {
            String header = httpServletRequest.getHeader(key);
            if (header != null) {
                return header;
            }
            return "";
        }
    };


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //这边过滤掉forward和避免上游重复配置了Filter
        if (servletRequest.getAttribute(SERVER_SPAN_CONTEXT) != null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 服务端降级
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("x-app-name", Systems.APP_NAME);
        response.addHeader("x-app-instance", Systems.APP_INSTANCE);

        String uri = request.getRequestURI();
        //是否满足排除条件
        if (isExclude(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
        int con = current.incrementAndGet(); //并发数
        if (con > 64) {
            String logMsg = "服务并发有点高,并发数:" + con;
            LOGGER.warn(logMsg);
            Alert.send(logMsg);
        }


        GrusServiceStatus grusServiceStatus = null;
        String reqAppName = request.getHeader(GrusServiceHttpHeader.REQ_APP_NAME);
        if (StringUtils.isNotEmpty(reqAppName)) {
            GrusRuntimeManager grusRuntimeManager = GrusFramework.getGrusRuntimeManager();
            grusServiceStatus = grusRuntimeManager.registerUpstreamService(reqAppName, "");
        }

        Tracer tracer = OpenTelemetrys.getTracer();
        Context context = OpenTelemetrys.getTextMapPropagator().extract(Context.current(), request, getter);
        Span span = tracer.spanBuilder(request.getMethod() + " " + uri).setParent(context).setSpanKind(SpanKind.SERVER).startSpan();


        request.setAttribute(SERVER_SPAN_CONTEXT, TAG_VALUE);

        //将Span信息放入容器
        if (span != Span.getInvalid()) {
            String traceId = span.getSpanContext().getTraceId();
            String spanId = span.getSpanContext().getSpanId();
            String parentId = "";
            if (span instanceof ReadWriteSpan readWriteSpan) {
                if (readWriteSpan.getParentSpanContext() != Span.getInvalid().getSpanContext()) {
                    parentId = readWriteSpan.getParentSpanContext().getSpanId();
                }
            }
            MDC.put("traceId", traceId);
            MDC.put("spanId", spanId);
            MDC.put("parentId", parentId);
            response.addHeader("x-trace-id", traceId);
            response.addHeader("x-span-id", spanId);
            response.addHeader("x-parent-id", parentId);
        }


        //执行应用处理
        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            span.setAttribute("component", "java-web-servlet");
            span.setAttribute("http.method", request.getMethod());
            span.setAttribute("http.scheme", request.getScheme());
            span.setAttribute("http.target", uri);

            filterChain.doFilter(request, response);

            if (grusServiceStatus != null) {
                grusServiceStatus.incrementSucceeded();
            }
        } catch (Throwable e) {
            if (grusServiceStatus != null) {
                grusServiceStatus.incrementFailed();
            }
            throw e;
        } finally {
            span.end();
            SlowLogger.logEvent(Module.SERVLET, span, request.getRequestURI());
            //结束清理
            MDC.clear();
            current.decrementAndGet();
        }
    }

    private static boolean isExclude(String uri) {
        return uri.contains("isLive") || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".ico") || uri.endsWith(".png") || uri.endsWith(".jpg");
    }
}
