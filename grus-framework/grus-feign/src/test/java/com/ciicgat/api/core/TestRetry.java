/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.kubernetes.KubernetesClientConfig;
import com.ciicgat.api.core.service.RetryService;
import feign.RetryableException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by August on 2021/9/15
 */
public class TestRetry {
    private static MockWebServer mockWebServer;

    private static RetryService retryService;

    @BeforeClass
    public static void init() {
        Pair<RetryService, MockWebServer> pair = TestUtil.newInstance("retry", RetryService.class);
        mockWebServer = pair.getRight();
        retryService = pair.getLeft();
    }

    @Test
    public synchronized void testRetry() throws IOException {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBody("2")
                .setResponseCode(200);
        mockWebServer.enqueue(mockResponse);
        Integer i = retryService.get();

        Assertions.assertEquals(2, i.intValue());

        mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBody("2")
                .setHeadersDelay(300, TimeUnit.MILLISECONDS)
                .setBodyDelay(300, TimeUnit.MILLISECONDS)
                .setResponseCode(400);
        mockWebServer.enqueue(mockResponse);
        try {
            i = retryService.get();
            System.out.println(i);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof RetryableException);
            final KubernetesClientConfig config = KubernetesClientConfig.getConfig();
            Assertions.assertFalse(config.couldRetry(e.getCause()));
        }


        mockWebServer.close();

        try {
            i = retryService.get();
            System.out.println(i);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof RetryableException);
            final KubernetesClientConfig config = KubernetesClientConfig.getConfig();
            Assertions.assertTrue(config.couldRetry(e.getCause()));
        }
    }


}
