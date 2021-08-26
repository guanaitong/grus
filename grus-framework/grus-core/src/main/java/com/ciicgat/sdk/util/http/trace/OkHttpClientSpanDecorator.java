/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http.trace;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import okhttp3.Request;
import okhttp3.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2018/8/22 17:50.
 */
public interface OkHttpClientSpanDecorator {


    void onRequest(Request request, Span span);

    void onError(Throwable throwable, Span span);


    void onResponse(Response response, Span span);

    OkHttpClientSpanDecorator STANDARD_TAGS = new OkHttpClientSpanDecorator() {
        @Override
        public void onRequest(Request request, Span span) {
            Tags.HTTP_METHOD.set(span, request.method());
            Tags.HTTP_URL.set(span, request.url().toString());
        }

        @Override
        public void onError(Throwable throwable, Span span) {
            Tags.ERROR.set(span, Boolean.TRUE);
            span.log(errorLogs(throwable));
        }

        @Override
        public void onResponse(Response response, Span span) {
            Tags.HTTP_STATUS.set(span, response.code());

            Tags.ERROR.set(span, response.code() >= 300);
        }

        protected Map<String, Object> errorLogs(Throwable throwable) {
            Map<String, Object> errorLogs = new HashMap<>(2);
            errorLogs.put("event", Tags.ERROR.getKey());
            errorLogs.put("error.object", throwable);

            return errorLogs;
        }
    };
}
