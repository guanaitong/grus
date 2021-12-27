/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http.trace;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.grus.service.GrusServiceHttpHeader;
import com.ciicgat.sdk.util.system.Systems;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Created by August.Zhou on 2018/8/22 18:01.
 */
public class OkhttpTracingInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(OkhttpTracingInterceptor.class);


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();

        Response response = null;


        requestBuilder.addHeader(GrusServiceHttpHeader.REQ_APP_NAME, Systems.APP_NAME);
        requestBuilder.addHeader(GrusServiceHttpHeader.REQ_APP_INSTANCE, Systems.APP_INSTANCE);
        requestBuilder.addHeader(GrusServiceHttpHeader.HTTP_UA_HEADER, "Grus okhttp client");

        Tracer tracer = OpenTelemetrys.getTracer();
        Span span = tracer.spanBuilder(originalRequest.method() + " " + originalRequest.url().encodedPath()).setSpanKind(SpanKind.CLIENT).startSpan();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending request {} on {} {}", originalRequest.url(), chain.connection(), originalRequest.headers());
        }
        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            span.setAttribute("component", "okhttp");
            span.setAttribute("http.method", originalRequest.method());
            span.setAttribute("http.url", originalRequest.url().toString());
            response = chain.proceed(requestBuilder.build());
        } catch (Throwable ex) {
            throw ex;
        } finally {
            span.end();
            SlowLogger.logEvent(Module.HTTP_CLIENT, span, originalRequest.toString());
            if (LOGGER.isDebugEnabled()) {
                if (response != null) {
                    LOGGER.debug("Received response for {} on {}", response.request().url(), response.headers());
                }
            }
        }

        return response;
    }

}

