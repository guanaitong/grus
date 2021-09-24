/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.grus.service.GrusFramework;
import com.ciicgat.grus.service.GrusRuntimeManager;
import com.ciicgat.grus.service.GrusServiceHttpHeader;
import com.ciicgat.grus.service.GrusServiceStatus;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import com.ciicgat.sdk.lang.tool.RollingNumber;
import com.ciicgat.sdk.servlet.fallback.FallbackHelper;
import com.ciicgat.sdk.servlet.trace.HttpServletRequestExtractAdapter;
import com.ciicgat.sdk.servlet.trace.ServletFilterSpanDecorator;
import com.ciicgat.sdk.trace.SpanUtil;
import com.ciicgat.sdk.trace.Spans;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
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
    private final AtomicInteger current = new AtomicInteger();
    private final RollingNumber rollingNumber = new RollingNumber();
    private ConfigCollection configCollection;

    private ServletFilterSpanDecorator spanDecorator = ServletFilterSpanDecorator.STANDARD_TAGS;

    public GrusFilter() {
        try {
            configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection();
        } catch (Exception e) {
            configCollection = null;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

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

        int con = current.incrementAndGet(); //并发数
        int qps = rollingNumber.record(); //qps
        if (con > 128) {
            String logMsg = "服务并发有点高,并发数:" + con + ",qps:" + qps;
            LOGGER.warn(logMsg);
            FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT, Module.SERVLET, logMsg, null);
            FrigateNotifier.sendMessageByAppName(logMsg);
        } else if (con > 64 && con > 4 * qps) {
            String logMsg = "服务性能有问题,并发数远远超过qps,并发数:" + con + ",qps:" + qps;
            LOGGER.warn(logMsg);
            FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT, Module.SERVLET, logMsg, null);
        }

        // 服务端降级
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (FallbackHelper.isUriNeedFallback(request.getRequestURI(), configCollection)) {
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().append(JSON.toJSONString(ErrorCode.REQUEST_BLOCK));
            return;
        }
        String uri = request.getRequestURI();

        //是否满足排除条件
        if (UriUtils.isExclude(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        GrusServiceStatus grusServiceStatus = null;
        String reqAppName = request.getHeader(GrusServiceHttpHeader.REQ_APP_NAME);
        if (StringUtils.isNotEmpty(reqAppName)) {
            GrusRuntimeManager grusRuntimeManager = GrusFramework.getGrusRuntimeManager();
            grusServiceStatus = grusRuntimeManager.registerUpstreamService(reqAppName, "");
        }

        Tracer tracer = GlobalTracer.get();

        SpanContext parentContext = tracer.extract(Format.Builtin.HTTP_HEADERS, new HttpServletRequestExtractAdapter(request));


        final Span span = tracer.buildSpan(request.getMethod())
                .asChildOf(parentContext)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER)
                .start();

        request.setAttribute(SERVER_SPAN_CONTEXT, span.context());

        //将Span信息放入容器
        Spans.setRootSpan(span, parentContext == null ? null : parentContext.baggageItems());

        spanDecorator.onRequest(request, response, span);


        //执行应用处理
        try (Scope scope = tracer.activateSpan(span)) {
            filterChain.doFilter(request, response);
            if (!request.isAsyncStarted()) {
                spanDecorator.onResponse(request, response, span);
            }

            if (grusServiceStatus != null) {
                grusServiceStatus.incrementSucceeded();
            }
        } catch (Throwable e) {
            spanDecorator.onError(request, response, e, span);
            if (grusServiceStatus != null) {
                grusServiceStatus.incrementFailed();
            }
            throw e;
        } finally {
            if (!request.isAsyncStarted()) {
                // If not async, then need to explicitly finish the span associated with the scope.
                // This is necessary, as we don't know whether this request is being handled
                // asynchronously until after the scope has already been started.
                span.finish();
                long duration = SpanUtil.getDurationMilliSeconds(span);
                SlowLogger.logEvent(Module.SERVLET, duration, request.getRequestURI());
            }
            //结束清理
            MDC.clear();
            Spans.remove();
            current.decrementAndGet();
        }
    }
}
