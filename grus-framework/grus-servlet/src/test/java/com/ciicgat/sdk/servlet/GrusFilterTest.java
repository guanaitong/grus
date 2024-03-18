/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.opentelemetry.OpenTelemetrys;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.concurrent.CountDownLatch;

/**
 * Created by August.Zhou on 2021/9/24 16:48.
 */
public class GrusFilterTest {
    private MockMvc mockMvc;

    @BeforeAll
    public static void registerTracer() {
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder().build();
        TextMapPropagator textMapPropagator = W3CTraceContextPropagator.getInstance();

        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).setPropagators(ContextPropagators.create(textMapPropagator)).build();

        Runtime.getRuntime().addShutdownHook(new Thread(sdkTracerProvider::close));
        GlobalOpenTelemetry.resetForTest();
        OpenTelemetrys.set(sdk);
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc =
                MockMvcBuilders.standaloneSetup(new TestController()).addFilters(new GrusFilter(true, new RequestIdProvider() {
                    @Override
                    public String getRequestId(HttpServletRequest httpServletRequest) {
                        return "test_";
                    }
                })).build();
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
//        String firstRequestTraceId = httpResp.getHeader("uber-trace-id");
//        Assertions.assertTrue(!firstRequestTraceId.isEmpty());

        String traceparent = httpResp.getHeader("traceparent");
        Assertions.assertTrue(!traceparent.isEmpty());
//        Assertions.assertEquals(firstRequestTraceId, firstRequestTraceId2);
//        System.out.println(firstRequestTraceId);
        Assertions.assertTrue(StringUtils.hasLength(httpResp.getHeader("x-trace-id")));
        Assertions.assertTrue(StringUtils.hasLength(httpResp.getHeader("x-span-id")));
        Assertions.assertFalse(StringUtils.hasLength(httpResp.getHeader("x-parent-id")));


        MockHttpServletRequestBuilder post1 = MockMvcRequestBuilders.post("/test?abc=1").params(params).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).header("traceparent", traceparent)
                .accept(MediaType.APPLICATION_JSON);
        MockHttpServletResponse httpResp1 = mockMvc.perform(post1).andReturn().getResponse();


        Assertions.assertTrue(StringUtils.hasLength(httpResp1.getHeader("x-trace-id")));
        Assertions.assertTrue(StringUtils.hasLength(httpResp1.getHeader("x-span-id")));
        Assertions.assertTrue(StringUtils.hasLength(httpResp1.getHeader("x-parent-id")));

        //两个请求，traceId应该相同
        Assertions.assertTrue(httpResp.getHeader("x-trace-id").contains(httpResp1.getHeader("x-trace-id")));
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

    @Test
    public void testHignConcurrency() throws Exception {
        int threadNum = 70;
        CountDownLatch latch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    MockHttpServletRequestBuilder post =
                            MockMvcRequestBuilders.post("/testConcurrency").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
                    MockHttpServletResponse httpResp = mockMvc.perform(post).andReturn().getResponse();
                    Assertions.assertEquals(HttpStatus.OK.value(), httpResp.getStatus());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        latch.await();
    }
}
