/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.HeaderService;
import com.ciicgat.grus.json.JSON;
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

/**
 * Created by August.Zhou on 2017/7/28 14:27.
 */

public class TestHeaderService {
    private static MockWebServer mockWebServer;
    private static HeaderService testService;

    @BeforeClass
    public static void init() {
        Pair<HeaderService, MockWebServer> pair = TestUtil.newInstance("get-delete", HeaderService.class);
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

        TestBean bean = testService.get("我的xx@");
        Assert.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //path encode的时候，
        Assert.assertEquals("/get/" + UrlCoder.encode("我的xx") + "@", recordedRequest.getPath());
        Assert.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assert.assertEquals("", bodyString);
    }

    @Test(expected = BusinessFeignException.class)
    public synchronized void testResponseHeader() {
        TestBean bean1 = new TestBean("jasdlfj", 91823);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("x-error-code", "1001")
                .addHeader("x-error-msg", "too big")
                .addHeader("x-api-version", "2.0")
                .setResponseCode(200)
                .setBody(JSON.toJSONString(bean1));
        mockWebServer.enqueue(mockResponse);

        testService.get("我的xx@");
    }


}
