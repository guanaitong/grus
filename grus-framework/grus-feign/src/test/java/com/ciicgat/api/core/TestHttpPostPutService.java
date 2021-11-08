/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.model.BodyBean;
import com.ciicgat.api.core.model.BodyBean2;
import com.ciicgat.api.core.model.DateBean;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.HttpPostPutService;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.constant.GatDateFormat;
import com.ciicgat.sdk.lang.url.UrlCoder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.jupiter.api.Assertions;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by August.Zhou on 2017/7/28 14:27.
 */

public class TestHttpPostPutService {
    private static MockWebServer mockWebServer;
    private static HttpPostPutService testService;

    @BeforeClass
    public static void init() {
        Pair<HttpPostPutService, MockWebServer> pair = TestUtil.newInstance("post-put", HttpPostPutService.class);
        mockWebServer = pair.getRight();
        testService = pair.getLeft();
    }

    @AfterClass
    public static void stop() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public synchronized void testPost() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(JSON.toJSONString(bean1));
        mockWebServer.enqueue(mockResponse);
        TestBean bean = testService.post("我的xx@", 456);
        Assertions.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/post", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals("text=" + UrlCoder.encode("我的xx@") +
                "&integer=" + 456, bodyString);
    }

    @Test
    public synchronized void testPostWithUrlParams() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(JSON.toJSONString(bean1));
        mockWebServer.enqueue(mockResponse);
        TestBean bean = testService.postWithUrlParams("我的xx@", 456);
        Assertions.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/postWithUrlParams?text=" + UrlCoder.encode("我的xx@"), recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals("integer=" + 456, bodyString);
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
        Assertions.assertEquals("/postBean", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals("integer=" + 456 + "&text=" + UrlCoder.encode("我的xx@"), bodyString);
    }

    @Test
    public synchronized void testPostBean2() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(JSON.toJSONString(bean1));
        mockWebServer.enqueue(mockResponse);
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        BodyBean2 bodyBean = new BodyBean2("我的xx@", 456, list);
        TestBean bean = testService.postBean2(bodyBean);
        Assertions.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/postBean2", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals("idList=" + UrlCoder.encode("1,2") + "&integer=" + 456 + "&text=" + UrlCoder.encode("我的xx@"), bodyString);
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
        Assertions.assertEquals("/postJson", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals(bodyString, JSON.toJSONString(bodyBean));
    }

    @Test
    public synchronized void testPostJsonWithUrlParams() {
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
        TestBean bean = testService.postJsonWithUrlParams("xxxyyy", bodyBean);
        Assertions.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/postJsonWithUrlParams?text=xxxyyy", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals(bodyString, JSON.toJSONString(bodyBean));
    }

    @Test
    public synchronized void testPut() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(JSON.toJSONString(bean1));
        mockWebServer.enqueue(mockResponse);
        TestBean bean = testService.put("我的xx@", 456);
        Assertions.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/put", recordedRequest.getPath());
        Assertions.assertEquals("PUT", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals("text=" + UrlCoder.encode("我的xx@") +
                "&integer=" + 456, bodyString);
    }

    @Test
    public synchronized void testPostWithListParams() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(JSON.toJSONString(bean1));
        mockWebServer.enqueue(mockResponse);
        List<String> stringList = Arrays.asList("我的xx@111", "我的xx@222");
        TestBean bean = testService.postWithListParams(stringList, 456);
        Assertions.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/postWithListParams", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals("texts=" + UrlCoder.encode(StringUtils.join(stringList, ",")) +
                "&integer=" + 456, bodyString);
    }

    @Test
    public synchronized void testRequestDate() throws Exception {
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
        Assertions.assertEquals("/requestWithDate", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertTrue(bodyString.contains(UrlCoder.encode("2020-01-01 00:00:00")));
    }
}
