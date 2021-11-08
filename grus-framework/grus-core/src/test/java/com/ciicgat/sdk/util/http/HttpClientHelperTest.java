/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;

import com.ciicgat.sdk.lang.tool.CloseUtils;
import com.ciicgat.sdk.util.system.WorkRegion;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author August
 * @date 2021/7/23 13:22
 */
public class HttpClientHelperTest  {
    private static final Logger log = LoggerFactory.getLogger(HttpClientHelperTest.class);

    @Test
    public void test() {
        long start = System.currentTimeMillis();
        String str = HttpClientHelper.get("https://www.tmall.com/");
        assertTrue(str.contains("天猫"));
        long start1 = System.currentTimeMillis();
        System.out.println(start1 - start);
        str = HttpClientHelper.get("https://www.baidu.com/");
        assertTrue(str.contains("baidu"));
        System.out.println(System.currentTimeMillis() - start1);
    }

    @Test
    public void testGuanaitongCC() {

        String publicDomainSuffix = WorkRegion.getCurrentWorkRegion().getPublicDomainSuffix();

        long start1 = System.currentTimeMillis();
        String str = HttpClientHelper.get(String.format("https://passport.guanaitong.%s/", publicDomainSuffix));
        assertTrue(str.contains("关爱通"));
        System.out.println(System.currentTimeMillis() - start1);

        long start2 = System.currentTimeMillis();
        str = HttpClientHelper.get(String.format("https://cas.ciicgat.%s/", publicDomainSuffix));
        assertTrue(str.contains("html"));
        System.out.println(System.currentTimeMillis() - start2);

        long start3 = System.currentTimeMillis();
        str = HttpClientHelper.get(String.format("https://nj.4008885818.%s/", publicDomainSuffix));
        assertTrue(str.contains("html"));
        System.out.println(System.currentTimeMillis() - start3);
    }


    /**
     * 11.11.11.11 cannot ping
     */
    public void testOkHttpClientConnectTimeoutOfIpNotPingAble() {
//        try {
//            HttpClientHelper.get("http://11.11.11.11:11122");
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//            log.error("error", e.getCause());
//            assertTrue(e.getCause() instanceof SocketTimeoutException);
//            assertTrue(e.getCause().getMessage().equals("connect timed out"));
//            return;
//        }
//        fail();
    }

    public void testOkHttpClientConnectTimeoutOfIpPingAble() {
        try {
            HttpClientHelper.postForm("http://127.0.0.1:11122", null, null);
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof ConnectException);
            assertTrue(e.getCause().getMessage().contains("Failed to connect to"));
            return;
        }
        Assertions.fail();
    }

//    public void testOkHttpClientOfServerDown() {
//        int port = 0;
//        while (true) {
//            try {
//                HttpClientHelper.get("http://127.0.0.1:19090/benchmark/base/string/helloFromWeb?delay=0", null, null);
//            } catch (RuntimeException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void testOkHttpClientOfServerDown1() {
        int port = 0;
        MockWebServer mockWebServer = null;
        while (true) {
            try {
                mockWebServer = new MockWebServer();
                port = new Random().nextInt(20000);
                mockWebServer.start(port);
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String url = "http://127.0.0.1:" + port;
        for (int i = 0; i < 10; i++) {
            MockResponse mockResponse = new MockResponse()
                    .addHeader("Content-Type", "application/json;charset=utf-8")
                    .setBody("1")
                    .setResponseCode(200);
            mockWebServer.enqueue(mockResponse);
            HttpClientHelper.get(url);
        }

        CloseUtils.close(mockWebServer);
        try {
            HttpClientHelper.get(url);
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof ConnectException);
            assertTrue(e.getCause().getMessage().contains("Failed to connect to"));
            return;
        }
        Assertions.fail();
    }


    public void testOkHttpClientOnServerError() {
        int port = 0;
        MockWebServer mockWebServer = null;
        while (true) {
            try {
                mockWebServer = new MockWebServer();
                port = new Random().nextInt(20000);
                mockWebServer.start(port);
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String url = "http://127.0.0.1:" + port;
        testSocketDisconnectAfterRequest(mockWebServer, url);
        testSocketTimeOut(mockWebServer, url);
        MockWebServer finalMockWebServer = mockWebServer;
        CompletableFuture.runAsync(() -> CloseUtils.close(finalMockWebServer));
    }

    private void testSocketDisconnectAfterRequest(MockWebServer mockWebServer, String url) {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST)
                .setResponseCode(200);
        mockWebServer.enqueue(mockResponse);
        try {
            HttpClientHelper.get(url);
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof IOException);
            assertTrue(e.getCause().getMessage().contains("unexpected end of stream"));
            return;
        }
        Assertions.fail();

    }

    private void testSocketTimeOut(MockWebServer mockWebServer, String url) {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setBody("2")
                .setBodyDelay(2, TimeUnit.DAYS)
                .setResponseCode(200);
        mockWebServer.enqueue(mockResponse);
        try {
            HttpClientHelper.get(url, null, null, new HttpTimeout().readTimeout(Duration.ofSeconds(1)));
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof SocketTimeoutException);
            assertTrue(e.getCause().getMessage().equals("timeout") || e.getCause().getMessage().equals("Read timed out"));
            return;
        }
        Assertions.fail();
    }
}
