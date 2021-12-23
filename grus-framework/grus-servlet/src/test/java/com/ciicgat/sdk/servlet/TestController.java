/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.sdk.lang.convert.ApiResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
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
        LOGGER.info("Test headers is:{}", Span.current());

        Context current = Context.current();
        Context root = Context.root();
        Map<String, String> headers = new HashMap<>();
        TextMapPropagator TEXT_MAP_PROPAGATOR = GlobalOpenTelemetry.get().getPropagators().getTextMapPropagator();
        TEXT_MAP_PROPAGATOR.inject(current, headers, new TextMapSetter<Map<String, String>>() {
            @Override
            public void set(@Nullable Map<String, String> carrier, String key, String value) {
                carrier.put(key, value);
            }
        });
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpServletResponse.addHeader(entry.getKey(), entry.getValue());
        }

        return ApiResponse.success("I am OK ...");
    }

    @RequestMapping(path = "/testJson")
    public ApiResponse<String> testJson(@RequestBody TestData testData) {
        LOGGER.info("Test data is:{}", testData);
        LOGGER.info("Test headers is:{}", Span.current());
        return ApiResponse.success("I am OK ...");
    }
}
