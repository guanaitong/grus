/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq.trace;

import com.ciicgat.sdk.trace.Spans;
import io.opentracing.Span;
import io.opentracing.tag.Tags;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2018/8/23 14:25.
 */
public interface ConsumerSpanDecorator {

    ConsumerSpanDecorator STANDARD_TAGS = new ConsumerSpanDecorator() {

        @Override
        public void onRequest(Span span) {
            Spans.setSystemTags(span);
            Tags.COMPONENT.set(span, "rabbitmq");
        }

        @Override
        public void onError(Throwable throwable, Span span) {
            Tags.ERROR.set(span, Boolean.TRUE);
            span.log(errorLogs(throwable));
        }

        @Override
        public void onResponse(Span span) {
        }

        protected Map<String, Object> errorLogs(Throwable throwable) {
            Map<String, Object> errorLogs = new HashMap<>(2);
            errorLogs.put("event", Tags.ERROR.getKey());
            errorLogs.put("error.object", throwable);

            return errorLogs;
        }
    };

    void onRequest(Span span);

    void onError(Throwable throwable, Span span);

    void onResponse(Span span);
}
