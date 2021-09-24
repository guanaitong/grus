/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.util.system.Systems;
import com.fasterxml.jackson.databind.JsonNode;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.samplers.RateLimitingSampler;
import io.opentracing.util.GlobalTracer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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

/**
 * Created by August.Zhou on 2021/9/24 16:48.
 */
public class GrusFilterTest {
    private MockMvc mockMvc;

    @BeforeClass
    public static void registerJaegerTracer() {
        Configuration.SenderConfiguration senderConfiguration =
                new Configuration
                        .SenderConfiguration()
                        .withAgentHost("127.0.0.1")
                        .withAgentPort(6831);

        Configuration.ReporterConfiguration reporterConfig =
                new Configuration
                        .ReporterConfiguration()
                        .withSender(senderConfiguration)
                        .withLogSpans(false);

        Float parm = "unknown".equals(Systems.APP_NAME) ? 0f : 50f;
        //采样配置
        Configuration.SamplerConfiguration samplerConfig =
                new Configuration
                        .SamplerConfiguration()
                        .withType(RateLimitingSampler.TYPE)
                        .withParam(parm);

        JaegerTracer tracer = new Configuration(Systems.APP_NAME).withSampler(samplerConfig).withReporter(reporterConfig).getTracer();
        GlobalTracer.register(tracer);
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);


        this.mockMvc = MockMvcBuilders.standaloneSetup(new TestController()).addFilters(new GrusFilter()).build();
    }

    @Test
    public void test() throws Exception {
        TestData testData = new TestData();
        testData.setStringData("abc");
        testData.setIntData(12);

        JsonNode jsonObject = JSON.parse(JSON.toJSONString(testData));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
//            params.add(entry.getKey(), entry.getValue().toString());
//        }

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders
                .post("/test?abc=1").params(params)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE);

        MockHttpServletResponse httpResp = mockMvc.perform(post).andReturn().getResponse();
        Assert.assertEquals(HttpStatus.OK.value(), httpResp.getStatus());
        Assert.assertTrue(!httpResp.getHeader("x-trace-id").isEmpty());
        String firstRequestTraceId = httpResp.getHeader("uber-trace-id");
        Assert.assertTrue(!firstRequestTraceId.isEmpty());
        System.out.println(firstRequestTraceId);


        MockHttpServletRequestBuilder post1 = MockMvcRequestBuilders
                .post("/test?abc=1").params(params)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header("uber-trace-id", firstRequestTraceId)//把uber-trace-id作为链追踪下去
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE);
        MockHttpServletResponse httpResp1 = mockMvc.perform(post1).andReturn().getResponse();

        //两个请求，traceId应该相同
        Assert.assertEquals(httpResp.getHeader("x-trace-id"), httpResp1.getHeader("x-trace-id"));
        //第一请求的span，是下一个请求的parent
        Assert.assertEquals(httpResp.getHeader("x-span-id"), httpResp1.getHeader("x-parent-id"));

    }

    @Test
    public void testJsonRequest() throws Exception {
        TestData testData = new TestData();
        testData.setStringData("abc");
        testData.setIntData(12);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/testJson?abc=1")
                .contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(testData))
                .accept(MediaType.APPLICATION_JSON);

        MockHttpServletResponse httpResp = mockMvc.perform(post).andReturn().getResponse();
        Assert.assertEquals(HttpStatus.OK.value(), httpResp.getStatus());
        Assert.assertTrue(!httpResp.getHeader("x-trace-id").isEmpty());
    }
}