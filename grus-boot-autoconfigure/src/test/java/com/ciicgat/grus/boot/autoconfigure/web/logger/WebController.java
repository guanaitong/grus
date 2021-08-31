/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.logger;

import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.trace.Spans;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.util.GlobalTracer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by August on 2021/8/31
 */
@RestController
@RequestMapping
public class WebController {
    @RequestMapping(path = "/test")
    public ApiResponse<String> test(String id, HttpServletResponse httpServletResponse) {

        Span span = Spans.getRootSpan();

        Tracer tracer = GlobalTracer.get();

        Map<String, String> headers = new HashMap<>();

        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new TextMapAdapter(headers));
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpServletResponse.addHeader(entry.getKey(), entry.getValue());
        }

        return ApiResponse.success("I am OK ...");
    }

}
