/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.model.BodyBean;
import com.ciicgat.api.core.model.DateBean;
import com.ciicgat.api.core.model.MvcDateBeanRequest;
import com.ciicgat.api.core.model.MvcDateBeanResponse;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.HttpMvcPostService;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.constant.GatDateFormat;
import com.ciicgat.sdk.lang.url.UrlCoder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Created by August.Zhou on 2017/7/28 14:27.
 */

public class TestHttpMvcPostService {
    private static MockWebServer mockWebServer;
    private static HttpMvcPostService testService;

    @BeforeAll
    public static void init() {
        Pair<HttpMvcPostService, MockWebServer> pair = TestUtil.newInstance("mvc-post", HttpMvcPostService.class);
        mockWebServer = pair.getRight();
        testService = pair.getLeft();
    }

    @AfterAll
    public static void stop() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public synchronized void testPostBean() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(JSON.toJSONString(bean1));
        mockWebServer.enqueue(mockResponse);
        BodyBean bodyBean = new BodyBean("我的xx@", 456);
        TestBean bean = testService.postBean(bodyBean);
        Assertions.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/mvc-post/form", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals("integer=" + 456 + "&text=" + UrlCoder.encode("我的xx@"), bodyString);
    }

    @Test
    public synchronized void testPostJson() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(JSON.toJSONString(bean1));
        mockWebServer.enqueue(mockResponse);
        BodyBean bodyBean = new BodyBean("我的xx@", 456);
        TestBean bean = testService.postJson(bodyBean);
        Assertions.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/mvc-post/postJson", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals(bodyString, JSON.toJSONString(bodyBean));
    }

    @Test
    public synchronized void testRequestWithDate() throws Exception {
        DateBean dateBean = new DateBean("jasdlfj", DateUtils.parseDate("2020-01-01 00:00:00", GatDateFormat.FULL_PATTERN));
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setBody("");
        mockWebServer.enqueue(mockResponse);
        TestBean bean = testService.requestWithDate(dateBean);
        Assertions.assertNull(bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/mvc-post/formRequestWithDate", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertTrue(bodyString.contains(UrlCoder.encode("2020-01-01 00:00:00")));
    }

    @Test
    public synchronized void testJsonRequestWithDate() throws Exception {
        DateBean dateBean = new DateBean("jasdlfj", DateUtils.parseDate("2020-01-01 00:00:00", GatDateFormat.FULL_PATTERN));
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setBody("");
        mockWebServer.enqueue(mockResponse);
        TestBean bean = testService.jsonRequestWithDate(dateBean);
        Assertions.assertNull(bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/mvc-post/jsonRequestWithDate", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertTrue(bodyString.contains(UrlCoder.encode("1577808000000")));
        Assertions.assertEquals(bodyString, JSON.toJSONString(dateBean));
    }

    @Test
    public synchronized void testJsonWithDate() throws Exception {
        MvcDateBeanRequest dateBean = new MvcDateBeanRequest("jasdlfj", DateUtils.parseDate("2020-01-01 00:00:00", GatDateFormat.FULL_PATTERN));
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setBody("");
        mockWebServer.enqueue(mockResponse);
        MvcDateBeanResponse bean = testService.jsonWithDate(dateBean);
        Assertions.assertNull(bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/mvc-post/jsonWithDate", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertTrue(bodyString.contains("2020-01-01 00:00:00"));
        Assertions.assertEquals(bodyString, JSON.toJSONString(dateBean));
    }

    @Test
    public synchronized void testValueString() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody("success\nabc\nbb");
        mockWebServer.enqueue(mockResponse);
        BodyBean bodyBean = new BodyBean("我的xx@", 456);
        String bean = testService.valueString(bodyBean);
        Assertions.assertEquals("success\nabc\nbb", bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/mvc-post/formString", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals("integer=" + 456 + "&text=" + UrlCoder.encode("我的xx@"), bodyString);
    }

}
