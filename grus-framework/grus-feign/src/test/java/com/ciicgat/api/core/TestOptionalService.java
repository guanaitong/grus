/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.OptionalService;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by August.Zhou on 2018-10-30 13:20.
 */
public class TestOptionalService {

    private static MockWebServer mockWebServer;
    private static OptionalService optionalService;

    @BeforeClass
    public static void init() {
        Pair<OptionalService, MockWebServer> pair = TestUtil.newInstance("optional", OptionalService.class);
        mockWebServer = pair.getRight();
        optionalService = pair.getLeft();
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

        Optional<TestBean> optional = optionalService.get();
        Assert.assertEquals(bean1, optional.get());
    }

    @Test
    public synchronized void testGet1() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setBody("");
        mockWebServer.enqueue(mockResponse);

        Optional<TestBean> optional = optionalService.get();
        Assert.assertFalse(optional.isPresent());
    }

    @Test
    public synchronized void testGetRespWithDataNull() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V1)
                .setBody(ApiResponse.success(null).toString());
        mockWebServer.enqueue(mockResponse);

        Optional<TestBean> optional = optionalService.getRespWithDataNull();
        Assert.assertFalse(optional.isPresent());
    }

    @Test
    public synchronized void testGetRespWithWrongHttpStatus() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(204)
                .setBody("");
        mockWebServer.enqueue(mockResponse);
        Optional<TestBean> optional = optionalService.getRespWithWrongHttpStatus();
        Assert.assertFalse(optional.isPresent());
    }
}
