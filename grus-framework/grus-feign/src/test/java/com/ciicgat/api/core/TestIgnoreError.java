/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.service.ErrorService;
import feign.FeignException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by August.Zhou on 2018-10-19 13:13.
 */
public class TestIgnoreError {

    private static MockWebServer mockWebServer;

    private static ErrorService errorService;

    @BeforeClass
    public static void init() {
        Pair<ErrorService, MockWebServer> pair = TestUtil.newInstance("error", ErrorService.class);
        mockWebServer = pair.getRight();
        errorService = pair.getLeft();
    }

    @AfterClass
    public static void stop() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public synchronized void testWithoutIgnoreErrorMeetRealError() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBody("2")
                .setResponseCode(501);
        mockWebServer.enqueue(mockResponse);

        Assertions.assertThrows(FeignException.class, () -> errorService.getWithOutIgnoreError());
    }

    @Test
    public synchronized void testWithIgnoreErrorMeetRealError() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(500);
        mockWebServer.enqueue(mockResponse);


        Integer i = errorService.getWithIgnoreError();
        Assertions.assertNull(i);
    }

    @Test
    public synchronized void testWithIgnoreErrorNotMeetRealError() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody("2")
                .setResponseCode(200);
        mockWebServer.enqueue(mockResponse);


        Integer i = errorService.getWithIgnoreError();
        Assertions.assertEquals(i.intValue(), 2);
    }


    @Test
    public synchronized void testGetIntWithIgnoreError() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(401);
        mockWebServer.enqueue(mockResponse);


        int i = errorService.getIntWithIgnoreError();
        Assertions.assertEquals(i, 0);
    }

    @Test
    public synchronized void testGetBoolenWithIgnoreError() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(404);
        mockWebServer.enqueue(mockResponse);

        boolean i = errorService.getBoolenWithIgnoreError();
        Assertions.assertTrue(!i);
    }

    @Test
    public synchronized void testGetVoidWithIgnoreError() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(405);
        mockWebServer.enqueue(mockResponse);
        errorService.getVoidWithIgnoreError();
    }

    @Test
    public synchronized void testGetOptionalWithIgnoreError() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(411);
        mockWebServer.enqueue(mockResponse);
        Optional<Integer> i = errorService.getOptionalWithIgnoreError();
        Assertions.assertTrue(!i.isPresent());
    }


}
