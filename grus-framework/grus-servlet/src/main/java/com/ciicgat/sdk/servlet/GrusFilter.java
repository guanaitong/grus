/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
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
import com.ciicgat.sdk.trace.TraceUtil;
import com.ciicgat.sdk.util.system.Systems;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;


/**
 * Created by August.Zhou on 2019-06-19 13:51.
 */
public class GrusFilter implements Filter, MeterBinder {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrusFilter.class);
    private static final String SERVER_SPAN_CONTEXT = GrusFilter.class.getName() + ".activeSpanContext";
    private static final Object TAG_VALUE = new Object();

    private static final SlowLogger<HttpServletRequest> SLOW_LOGGER = new SlowLogger<>(Module.SERVLET, request -> request.getRequestURI());
    private final AtomicInteger concurrency = new AtomicInteger();

    private final LongAdder highConcurrencyEvents = new LongAdder();

    private final boolean disableOldSslProtocol;
    private final RequestIdProvider requestIdProvider;

    public GrusFilter(boolean disableOldSslProtocol) {
        this(disableOldSslProtocol, RequestIdProvider.NONE);
    }

    public GrusFilter(boolean disableOldSslProtocol, RequestIdProvider requestIdProvider) {
        this.disableOldSslProtocol = disableOldSslProtocol;
        this.requestIdProvider = Objects.requireNonNull(requestIdProvider);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        FunctionCounter.builder("grus.web.high.concurrency.event", this, GrusFilter::getHighConcurrencyEvents).baseUnit(BaseUnits.EVENTS).register(registry);
    }

    public long getHighConcurrencyEvents() {
        return highConcurrencyEvents.longValue();
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
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (disableOldSslProtocol && Systems.IN_K8S) {
            // 下面两个header由网关传过来的
            String scheme = request.getHeader("x-scheme");
            String sslProtocol = request.getHeader("x-ssl-protocol");
            if ("https".equals(scheme) && sslProtocol != null && !sslProtocol.isEmpty()) {
                if (!"TLSv1.2".equals(sslProtocol) && !"TLSv1.3".equals(sslProtocol)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(String.format("ssl version %s not support", sslProtocol));
                    return;
                }
            }
        }

        response.addHeader("x-app-name", Systems.APP_NAME);
        response.addHeader("x-app-instance", Systems.APP_INSTANCE);

        String uri = request.getRequestURI();

        //是否满足排除条件
        if (isExclude(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        //流量计数
        TrafficCounter.trafficIncrement();

        int con = concurrency.incrementAndGet(); //并发数
        if (con > 64) {
            String logMsg = "服务并发有点高,并发数:" + con;
            LOGGER.warn(logMsg);
            Alert.send(logMsg);
            highConcurrencyEvents.increment();
        }

        GrusServiceStatus grusServiceStatus = null;
        String reqAppName = request.getHeader(GrusServiceHttpHeader.REQ_APP_NAME);
        if (StringUtils.isNotEmpty(reqAppName)) {
            GrusRuntimeManager grusRuntimeManager = GrusFramework.getGrusRuntimeManager();
            grusServiceStatus = grusRuntimeManager.registerUpstreamService(reqAppName, "");
        }

        Tracer tracer = OpenTelemetrys.getTracer();
        Context context = OpenTelemetrys.getTextMapPropagator().extract(Context.current(), request, getter);
        String requestId = requestIdProvider.getRequestId(request);
        if (requestId != null) {
            context = TraceUtil.withRequestContext(context, requestId);
        } else {
            requestId = TraceUtil.getRequestIdId(context);
        }
        Span span =
                tracer.spanBuilder(request.getMethod() + ":" + uri).setParent(context).setSpanKind(SpanKind.SERVER).startSpan();
        request.setAttribute(SERVER_SPAN_CONTEXT, TAG_VALUE);

        // 两个makeCurrent的效果，就是其他地方可以随时获取当前span。同时开启子的span时，无需显示的调用setParent
        try (Scope ignored = context.makeCurrent()) { //将context绑定到全局ContextStorage中
            //执行应用处理
            try (Scope scope = span.makeCurrent()) { //将span绑定到当前的context中
                //将相关信息放入span容器
                if (span != Span.getInvalid()) {
                    TraceUtil.putMdcForTrace(span, requestId);
                    response.addHeader("x-trace-id", TraceUtil.getTraceId(span));
                    response.addHeader("x-span-id", TraceUtil.getSpanId(span));
                    String parentId = TraceUtil.getParentId(span);
                    if (StringUtils.isNotEmpty(parentId)) {
                        response.addHeader("x-parent-id", parentId);
                    }
                }
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
                SLOW_LOGGER.mayLog(span, Map.of("method", request.getMethod(), "url", request.getRequestURI()), request);
                //结束清理
                MDC.clear();
                concurrency.decrementAndGet();
            }
        }
    }

    private static boolean isExclude(String uri) {
        return uri.contains("isLive") || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".ico") || uri.endsWith(".png") || uri.endsWith(".jpg");
    }

}
