/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.DefaultMethodService;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.url.UrlCoder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.jupiter.api.Assertions;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Created by August.Zhou on 2021/9/23 16:55.
 */
public class TestDefaultMethodService {

    private static MockWebServer mockWebServer;
    private static DefaultMethodService testService;

    @BeforeClass
    public static void init() {
        Pair<DefaultMethodService, MockWebServer> pair = TestUtil.newInstance("default-method", DefaultMethodService.class);
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

        TestBean bean = testService.get1("我的xx@", 456);
        Assertions.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //path encode的时候，
        Assertions.assertEquals("/get/" + UrlCoder.encode("我的xx@") + "?count=456", recordedRequest.getPath());
        Assertions.assertEquals("GET", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals("", bodyString);
    }
}
