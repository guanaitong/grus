/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * Created by August.Zhou on 2021/9/24 16:48.
 */
public class GrusFilterTest {
    private MockMvc mockMvc;

    @BeforeAll
    public static void registerTracer() {
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder().build();
        TextMapPropagator textMapPropagator = TextMapPropagator.composite(W3CTraceContextPropagator.getInstance(), JaegerPropagator.getInstance());

        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).setPropagators(ContextPropagators.create(textMapPropagator)).build();

        Runtime.getRuntime().addShutdownHook(new Thread(sdkTracerProvider::close));
        GlobalOpenTelemetry.resetForTest();
        OpenTelemetrys.set(sdk);
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TestController()).addFilters(new GrusFilter()).build();
    }

    @Test
    public void test() throws Exception {
        TestData testData = new TestData();
        testData.setStringData("abc");
        testData.setIntData(12);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
//            params.add(entry.getKey(), entry.getValue().toString());
//        }

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/test?abc=1").params(params).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).accept(MediaType.APPLICATION_JSON);

        MockHttpServletResponse httpResp = mockMvc.perform(post).andReturn().getResponse();
        Assertions.assertEquals(HttpStatus.OK.value(), httpResp.getStatus());
        Assertions.assertTrue(!httpResp.getHeader("x-trace-id").isEmpty());
        String firstRequestTraceId = httpResp.getHeader("uber-trace-id");
        Assertions.assertTrue(!firstRequestTraceId.isEmpty());

        String firstRequestTraceId2 = httpResp.getHeader("traceparent");
        Assertions.assertTrue(!firstRequestTraceId2.isEmpty());
//        Assertions.assertEquals(firstRequestTraceId, firstRequestTraceId2);
        System.out.println(firstRequestTraceId);
        Assertions.assertTrue(StringUtils.hasLength(httpResp.getHeader("x-trace-id")));
        Assertions.assertTrue(StringUtils.hasLength(httpResp.getHeader("x-span-id")));
        Assertions.assertFalse(StringUtils.hasLength(httpResp.getHeader("x-parent-id")));


        MockHttpServletRequestBuilder post1 = MockMvcRequestBuilders.post("/test?abc=1").params(params).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).header("uber-trace-id", firstRequestTraceId)//把uber-trace-id作为链追踪下去
                .accept(MediaType.APPLICATION_JSON);
        MockHttpServletResponse httpResp1 = mockMvc.perform(post1).andReturn().getResponse();


        Assertions.assertTrue(StringUtils.hasLength(httpResp1.getHeader("x-trace-id")));
        Assertions.assertTrue(StringUtils.hasLength(httpResp1.getHeader("x-span-id")));
        Assertions.assertTrue(StringUtils.hasLength(httpResp1.getHeader("x-parent-id")));

        //两个请求，traceId应该相同
        Assertions.assertEquals(httpResp.getHeader("x-trace-id"), httpResp1.getHeader("x-trace-id"));
        //第一请求的span，是下一个请求的parent
        Assertions.assertEquals(httpResp.getHeader("x-span-id"), httpResp1.getHeader("x-parent-id"));

    }

    @Test
    public void testJsonRequest() throws Exception {
        TestData testData = new TestData();
        testData.setStringData("abc");
        testData.setIntData(12);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/testJson?abc=1").contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(testData)).accept(MediaType.APPLICATION_JSON);

        MockHttpServletResponse httpResp = mockMvc.perform(post).andReturn().getResponse();
        Assertions.assertEquals(HttpStatus.OK.value(), httpResp.getStatus());
        Assertions.assertTrue(!httpResp.getHeader("x-trace-id").isEmpty());
    }
}
