/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq.trace;

import com.rabbitmq.client.AMQP;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;

import java.util.HashMap;
import java.util.Map;

public class TracingUtils {
    public static AMQP.BasicProperties inject(AMQP.BasicProperties properties, Span span,
                                              Tracer tracer) {

        Map<String, Object> headers = new HashMap<>();

        tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new MapHeadersAdapter(headers));

        if (headers.size() == 0) {
            return properties;
        }

        if (properties == null) {
            return new AMQP.BasicProperties("application/octet-stream",
                    null,
                    headers,
                    2,
                    0, null, null, null,
                    null, null, null, null,
                    null, null);
        }

        if (properties.getHeaders() != null) {
            headers.putAll(properties.getHeaders());
        }

        return properties.builder()
                .headers(headers)
                .build();
    }
}
