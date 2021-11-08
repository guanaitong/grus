/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.HeaderService;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.language.LanguageConstant;
import com.ciicgat.grus.language.LanguageThreadLocal;
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
import java.util.Locale;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/03/17 10:33
 * @Description:
 */
public class TestLanguageInterceptor {
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
    public synchronized void testEnglish() {
        LanguageThreadLocal.setLocale(Locale.ENGLISH);

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
        Assertions.assertEquals(bean1, bean);

        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // path encode的时候，
        Assertions.assertEquals("/get/" + UrlCoder.encode("我的xx@"), recordedRequest.getPath());
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        String bodyString = recordedRequest.getBody().readUtf8();
        Assertions.assertEquals("", bodyString);

        // gconf开关未打开，无header
        Assertions.assertNull(recordedRequest.getHeader(LanguageConstant.LANG_HEADER));
    }
}
