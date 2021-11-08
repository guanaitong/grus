/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.service.TimeoutService;
import feign.FeignException;
import feign.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.jupiter.api.Assertions;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2018-10-10 13:45.
 */
public class TestTimeout {

    private static MockWebServer mockWebServer;
    private static TimeoutService testService;

    private static MockWebServer mockWebServer1;
    private static TimeoutService testService1;

    @BeforeClass
    public static void init() {
        Pair<TimeoutService, MockWebServer> pair = TestUtil.newInstance("timeout", TimeoutService.class);
        mockWebServer = pair.getRight();
        testService = pair.getLeft();

        Pair<TimeoutService, MockWebServer> pair1 = TestUtil.newInstance("timeout", () -> FeignServiceFactory.newInstance(TimeoutService.class, new Request.Options(1000, 2000)));
        mockWebServer1 = pair1.getRight();
        testService1 = pair1.getLeft();
    }

    @AfterClass
    public static void stop() throws IOException {
        mockWebServer.shutdown();
        mockWebServer1.shutdown();
    }


    @Test
    public void testAnnotationTimeOut() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBodyDelay(2, TimeUnit.SECONDS)
                .setBody("2")
                .setResponseCode(200);
        mockWebServer.enqueue(mockResponse);
        FeignException e = null;
        try {
            testService.getWithApiTimeoutAnnotation();
        } catch (FeignException e1) {
            e = e1;
        }
        Assertions.assertNotNull(e);
        Assertions.assertTrue(e.getCause() instanceof SocketTimeoutException);

    }

    @Test
    public void testAnnotationNotTimeOut() {
        int i = new Random().nextInt(100000);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBodyDelay(2, TimeUnit.SECONDS)
                .setBody(String.valueOf(i))
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setResponseCode(200);
        mockWebServer.enqueue(mockResponse);
        int j = testService.get();
        Assertions.assertEquals(i, j);
    }

    @Test
    public void testConfigTimeOut() {
        int i = new Random().nextInt(100000);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBodyDelay(1, TimeUnit.SECONDS)
                .setBody(String.valueOf(i))
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setResponseCode(200);
        mockWebServer1.enqueue(mockResponse);
        int j = testService1.get();
        Assertions.assertEquals(i, j);
    }

    @Test
    public void testConfigNotTimeOut() {
        int i = new Random().nextInt(100000);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBodyDelay(4, TimeUnit.SECONDS)
                .setBody(String.valueOf(i))
                .setResponseCode(200);
        mockWebServer1.enqueue(mockResponse);
        Assertions.assertThrows(FeignException.class, () -> testService1.get());
    }
}
