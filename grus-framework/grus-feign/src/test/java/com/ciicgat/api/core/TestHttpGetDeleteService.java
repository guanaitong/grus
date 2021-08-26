/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.HttpGetDeleteService;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.convert.Pagination;
import com.ciicgat.sdk.lang.url.UrlCoder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by August.Zhou on 2017/7/28 14:27.
 */

public class TestHttpGetDeleteService {
    private static MockWebServer mockWebServer;
    private static HttpGetDeleteService testService;

    @BeforeClass
    public static void init() {
        Pair<HttpGetDeleteService, MockWebServer> pair = TestUtil.newInstance("get-delete", HttpGetDeleteService.class);
        mockWebServer = pair.getRight();
        testService = pair.getLeft();
    }

    @AfterClass
    public static void stop() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    public synchronized void testGet() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(JSON.toJSONString(bean1));
        mockWebServer.enqueue(mockResponse);

        TestBean bean = testService.get("我的xx@", 456);
        Assert.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //path encode的时候，
        Assert.assertEquals("/get/" + UrlCoder.encode("我的xx") + "@" + "?count=456", recordedRequest.getPath());
        Assert.assertEquals("GET", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assert.assertEquals("", bodyString);
    }


    @Test(expected = IllegalArgumentException.class)
    public synchronized void testGetUnNormal() {
        testService.getUnNormal("我的xx@", 456);
    }

    @Test
    public synchronized void testDelete() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(JSON.toJSONString(bean1));
        mockWebServer.enqueue(mockResponse);

        TestBean bean = testService.delete("我的xx@", 456);
        Assert.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("/delete/" + UrlCoder.encode("我的xx") + "@" + "?count=456", recordedRequest.getPath());
        Assert.assertEquals("DELETE", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assert.assertEquals("", bodyString);
    }

    @Test
    public synchronized void testGetWithApiRespData() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V1)
                .setBody(ApiResponse.success(bean1).toString());
        mockWebServer.enqueue(mockResponse);

        TestBean bean = testService.getWithApiRespData();
        Assert.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("/getWithApiRespData", recordedRequest.getPath());
        Assert.assertEquals("GET", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assert.assertEquals("", bodyString);
    }

    @Test
    public synchronized void testGetWithApiRespDataCodeNotZero() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 222)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "error")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(ApiResponse.fail(222, "error").toString());
        mockWebServer.enqueue(mockResponse);
        BusinessFeignException e = null;
        TestBean bean = null;
        try {
            bean = testService.getWithApiRespDataCodeNotZero();
        } catch (BusinessFeignException e1) {
            e = e1;
        }

        Assert.assertNotNull(e);
        Assert.assertNull(bean);
        Assert.assertEquals(222, e.getErrorCode());

        try {
            mockWebServer.takeRequest();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }

    }


    @Test
    public synchronized void testGetBeanList() {
        TestBean bean1 = new TestBean("我的你的阿历克斯江东父老；扩大社交分类；卡斯蒂洛；价格", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(JSON.toJSONString(Arrays.asList(bean1)));
        mockWebServer.enqueue(mockResponse);

        List<TestBean> beans = testService.getBeanList();
        Assert.assertEquals(bean1, beans.get(0));

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("/getBeanList", recordedRequest.getPath());
        Assert.assertEquals("GET", recordedRequest.getMethod());
    }

    @Test
    public synchronized void testGetBeanPagination() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody(JSON.toJSONString(new Pagination<TestBean>(1, Arrays.asList(bean1))));
        mockWebServer.enqueue(mockResponse);

        Pagination<TestBean> beans = testService.getBeanPagination();
        Assert.assertEquals(bean1, beans.getDataList().get(0));

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("/getBeanPagination", recordedRequest.getPath());
        Assert.assertEquals("GET", recordedRequest.getMethod());
    }


    @Test
    public synchronized void testGetApiResponseOfBeanList() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setBody(ApiResponse.success(Arrays.asList(bean1)).toString());
        mockWebServer.enqueue(mockResponse);

        ApiResponse<List<TestBean>> bean = testService.getApiResponseOfBeanList();
        Assert.assertEquals(bean1, bean.getData().get(0));

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("/getApiResponseOfBeanList", recordedRequest.getPath());
        Assert.assertEquals("GET", recordedRequest.getMethod());

    }

    @Test
    public synchronized void testGetBeanListWithApiRespData() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V1)
                .setBody(ApiResponse.success(Arrays.asList(bean1)).toString());
        mockWebServer.enqueue(mockResponse);

        List<TestBean> bean = testService.getBeanListWithApiRespData();
        Assert.assertEquals(bean1, bean.get(0));

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("/getBeanListWithApiRespData", recordedRequest.getPath());
        Assert.assertEquals("GET", recordedRequest.getMethod());
    }


    @Test
    public synchronized void testGetWithListParams() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setResponseCode(200)
                .setBody(JSON.toJSONString(bean1));
        mockWebServer.enqueue(mockResponse);


        TestBean bean = testService.getWithListParams(Arrays.asList("server1", "server2"), 456);
        Assert.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("/get?serverIds=server1&serverIds=server2&count=456", recordedRequest.getPath());
        Assert.assertEquals("GET", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assert.assertEquals("", bodyString);
    }

}
