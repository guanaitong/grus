/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet.trace;

import com.ciicgat.grus.core.Module;
import com.ciicgat.sdk.trace.SpanDecorator;
import com.ciicgat.sdk.trace.SpanUtil;
import com.ciicgat.sdk.trace.Spans;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import com.ciicgat.sdk.util.system.Systems;
import io.opentracing.Span;
import io.opentracing.tag.Tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2018/8/15 11:42.
 */
public interface ServletFilterSpanDecorator extends SpanDecorator {


    void onRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Span span);


    void onResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Span span);


    void onError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                 Throwable exception, Span span);


    ServletFilterSpanDecorator STANDARD_TAGS = new ServletFilterSpanDecorator() {
        @Override
        public void onRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Span span) {
            Spans.setSystemTags(span);

            Tags.COMPONENT.set(span, "java-web-servlet");

            Tags.HTTP_METHOD.set(span, httpServletRequest.getMethod());

            //without query params
            Tags.HTTP_URL.set(span, httpServletRequest.getRequestURL().toString());


            httpServletResponse.addHeader("x-app-name", Systems.APP_NAME);
            httpServletResponse.addHeader("x-app-instance", Systems.APP_INSTANCE);
            if (SpanUtil.isNoop(span)) {
                return;
            }

            String traceId = SpanUtil.getTraceId(span);
            String spanId = SpanUtil.getSpanId(span);
            String parentId = SpanUtil.getParentId(span);

            httpServletResponse.addHeader("x-trace-id", traceId);
            httpServletResponse.addHeader("x-span-id", spanId);
            httpServletResponse.addHeader("x-parent-id", parentId);
        }

        @Override
        public void onResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                               Span span) {
            Tags.HTTP_STATUS.set(span, httpServletResponse.getStatus());
            Tags.ERROR.set(span, httpServletResponse.getStatus() >= 400);
            if (httpServletResponse.getStatus() >= 500) {
                FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT, Module.SERVLET, String.format("server error，statusCode:%d,requestURI:%s", httpServletResponse.getStatus(), httpServletRequest.getRequestURI()), null);
            }
        }

        @Override
        public void onError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                            Throwable exception, Span span) {
            Tags.HTTP_STATUS.set(span, httpServletResponse.getStatus());
            Tags.ERROR.set(span, Boolean.TRUE);
            span.log(logsForException(exception));
            if (httpServletResponse.getStatus() >= 500) {
                FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT, Module.SERVLET, String.format("server error，statusCode:%d,requestURI:%s", httpServletResponse.getStatus(), httpServletRequest.getRequestURI()), null);
            }
        }


        private Map<String, String> logsForException(Throwable throwable) {
            Map<String, String> errorLog = new HashMap<>(3);
            errorLog.put("event", Tags.ERROR.getKey());

            String message = throwable.getCause() != null ? throwable.getCause().getMessage() : throwable.getMessage();
            if (message != null) {
                errorLog.put("message", message);
            }
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            errorLog.put("stack", sw.toString());

            return errorLog;
        }
    };


}
