/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.trace.Spans;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Jiaju.Wei
 * @Date: Created in 2018/6/6
 * @Description:
 */
@RestController
@RequestMapping
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(path = "/test")
    public ApiResponse<String> test(TestData testData, HttpServletResponse httpServletResponse) {

        LOGGER.info("Test data is:{}", testData);
        LOGGER.info("Test headers is:{}", Spans.getRootSpan());

        Span span = Spans.getRootSpan();

        Tracer tracer = GlobalTracer.get();

        Map<String, String> headers = new HashMap<>();

        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new TextMapAdapter(headers));
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpServletResponse.addHeader(entry.getKey(), entry.getValue());
        }

        return ApiResponse.success("I am OK ...");
    }

    @RequestMapping(path = "/testJson")
    public ApiResponse<String> testJson(@RequestBody TestData testData, HttpServletResponse httpServletResponse) {

        LOGGER.info("Test data is:{}", testData);
        LOGGER.info("Test headers is:{}", Spans.getRootSpan());
        return ApiResponse.success("I am OK ...");
    }
}
