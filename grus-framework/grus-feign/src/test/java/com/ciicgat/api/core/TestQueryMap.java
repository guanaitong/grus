/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.service.QueryMapService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2017/8/31 10:17.
 */

public class TestQueryMap {
    private static MockWebServer mockWebServer;
    private static QueryMapService queryMapService;

    @BeforeAll
    public static void init() {
        Pair<QueryMapService, MockWebServer> pair = TestUtil.newInstance("querymap", QueryMapService.class);
        mockWebServer = pair.getRight();
        queryMapService = pair.getLeft();
    }

    @AfterAll
    public static void stop() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public synchronized void testGet() throws Exception {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody("true");

        mockWebServer.enqueue(mockResponse);


        Map<String, Object> map = new HashMap<>();
        map.put("other", 123123);
        Boolean r = queryMapService.get("asdfasdfasdf", map);
        Assertions.assertTrue(r);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/send?parameterName=asdfasdfasdf&other=123123", recordedRequest.getPath());
        Assertions.assertEquals("GET", recordedRequest.getMethod());


    }

    @Test
    public synchronized void testPost() throws Exception {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setBody("true");

        mockWebServer.enqueue(mockResponse);


        Map<String, Object> map = new HashMap<>();
        map.put("other", 123123);
        Boolean r = queryMapService.post("asdfasdfasdf", map);
        Assertions.assertTrue(r);


        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals("/send?other=123123", recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals("parameterName=asdfasdfasdf", bodyString);

    }

}
