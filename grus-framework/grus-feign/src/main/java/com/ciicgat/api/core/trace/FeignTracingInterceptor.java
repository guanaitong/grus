/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.trace;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.grus.service.GrusServiceHttpHeader;
import com.ciicgat.sdk.util.system.Systems;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Created by August.Zhou on 2018/8/22 18:01.
 */
public class FeignTracingInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeignTracingInterceptor.class);
    private static final TextMapPropagator textMapPropagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();
    private static final TextMapSetter<Request.Builder> setter = (carrier, key, value) -> carrier.addHeader(key, value);


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();
        requestBuilder.addHeader(GrusServiceHttpHeader.REQ_APP_NAME, Systems.APP_NAME);
        requestBuilder.addHeader(GrusServiceHttpHeader.REQ_APP_INSTANCE, Systems.APP_INSTANCE);
        requestBuilder.addHeader(GrusServiceHttpHeader.HTTP_UA_HEADER, "Grus service client");

        Tracer tracer = OpenTelemetrys.get();
        Span span = tracer.spanBuilder(originalRequest.method() + " " + originalRequest.url().encodedPath()).setSpanKind(SpanKind.CLIENT).startSpan();
        try (Scope scope = span.makeCurrent()) {
            OpenTelemetrys.configSystemTags(span);
            span.setAttribute("http.method", originalRequest.method());
            span.setAttribute("component", "feign");
            textMapPropagator.inject(Context.current(), requestBuilder, setter);
            return chain.proceed(requestBuilder.build());
        } finally {
            span.end();
            SlowLogger.logEvent(Module.FEIGN, span, originalRequest.toString());
        }
    }

}

