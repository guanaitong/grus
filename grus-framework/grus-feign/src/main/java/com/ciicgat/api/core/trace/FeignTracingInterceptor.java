/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.trace;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.sdk.trace.SpanUtil;
import com.ciicgat.sdk.trace.Spans;
import com.ciicgat.sdk.util.http.trace.OkHttpClientSpanDecorator;
import com.ciicgat.sdk.util.http.trace.RequestBuilderInjectAdapter;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopSpan;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
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
    private static final Logger LOGGER = LoggerFactory.getLogger("FeignClient");

    private OkHttpClientSpanDecorator decorator = OkHttpClientSpanDecorator.STANDARD_TAGS;


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = chain.request().newBuilder();

        Response response = null;

        Tracer tracer = GlobalTracer.get();

        Span rootSpan = Spans.getRootSpan();
        final Span span = tracer.buildSpan(originalRequest.method())
                .asChildOf(rootSpan == NoopSpan.INSTANCE ? null : rootSpan)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                .withTag(Tags.COMPONENT.getKey(), "feign")
                .start();
        Spans.setSystemTags(span);

        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new RequestBuilderInjectAdapter(requestBuilder));

        Request newRequest = requestBuilder.build();
        decorator.onRequest(chain.request(), span);

        try {
            response = chain.proceed(newRequest);
            decorator.onResponse(response, span);
        } catch (Throwable ex) {
            decorator.onError(ex, span);
            throw ex;
        } finally {
            span.finish();
            long durationMilliSeconds = SpanUtil.getDurationMilliSeconds(span);
            SlowLogger.logEvent(Module.FEIGN, durationMilliSeconds, originalRequest.toString());
        }


        return response;
    }

}

