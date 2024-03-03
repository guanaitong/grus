/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.FlowService;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.sentinel.FlowRuleOptions;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import feign.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @Author: meizhiqing
 * @Date: Created in 2024/3/2
 * @Description:
 */
public class TestSentinelFlow {

    /**
     * 模拟qps限流
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testMethodOfQps() throws IOException, InterruptedException {
        FlowRuleOptions grusDegradeRule = new FlowRuleOptions();
        // Set max qps to 20
        grusDegradeRule.setCount(20)
                .setGrade(RuleConstant.FLOW_GRADE_QPS);
        Pair<FlowService, MockWebServer> pair = TestUtil.newInstance("flow-test", new Supplier<FlowService>() {
            @Override
            public FlowService get() {
                FlowService flowService = FeignServiceBuilder.newBuilder().serviceClazz(FlowService.class)
                        .options(new Request.Options(1000, 10_000))
                        .addMethodRule("testMethodOfQps", Set.of(grusDegradeRule)).build();
                return flowService;
            }
        });
        MockWebServer mockWebServer = pair.getRight();
        FlowService flowService = pair.getLeft();

        TestBean bean1 = new TestBean("jasdlfj", 91823);
//        mockResponse响应时间是2s
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setHeadersDelay(10, TimeUnit.MICROSECONDS)
                .setBody(JSON.toJSONString(bean1));
        for (int i = 0; i < 10000; i++) {
            mockWebServer.enqueue(mockResponse);
        }
        final AtomicBoolean existBlockException = new AtomicBoolean(false);
//        必须启动一个线程池来调用接口，如果单线程调用，是不满足单位时间内最小请求数要求的
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int num = 50;
        CountDownLatch countDownLatch = new CountDownLatch(num);
        for (int i = 0; i < num; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TestBean xx = flowService.testMethodOfQps();
                        System.out.println(xx);
                    } catch (BusinessRuntimeException e) {
                        if (e.getErrorCode() == ErrorCode.REQUEST_BLOCK.getErrorCode()) {
                            existBlockException.set(true);
                        } else {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });

        }
        countDownLatch.await();
        mockWebServer.shutdown();
        executorService.shutdown();
        Assertions.assertTrue(existBlockException.get());
    }

    /**
     * 模拟并发限流
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testMethodOfConcurrency() throws IOException, InterruptedException {
        FlowRuleOptions grusDegradeRule = new FlowRuleOptions();
        // Set max thread to 5
        grusDegradeRule.setCount(5)
                .setGrade(RuleConstant.FLOW_GRADE_THREAD);
        Pair<FlowService, MockWebServer> pair = TestUtil.newInstance("flow-test", new Supplier<FlowService>() {
            @Override
            public FlowService get() {
                FlowService flowService = FeignServiceBuilder.newBuilder().serviceClazz(FlowService.class)
                        .options(new Request.Options(1000, 10_000))
                        .addMethodRule("testMethodOfConcurrency", Set.of(grusDegradeRule)).build();
                return flowService;
            }
        });
        MockWebServer mockWebServer = pair.getRight();
        FlowService flowService = pair.getLeft();

        TestBean bean1 = new TestBean("jasdlfj", 91823);
//        mockResponse响应时间是2s
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setHeadersDelay(10, TimeUnit.MICROSECONDS)
                .setBody(JSON.toJSONString(bean1));
        for (int i = 0; i < 10000; i++) {
            mockWebServer.enqueue(mockResponse);
        }
        final AtomicBoolean existBlockException = new AtomicBoolean(false);
//        必须启动一个线程池来调用接口，如果单线程调用，是不满足单位时间内最小请求数要求的
//        如果以下的线程池的的个数小于5，那么不会触发限流
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        int num = 50;
        CountDownLatch countDownLatch = new CountDownLatch(num);
        for (int i = 0; i < num; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TestBean xx = flowService.testMethodOfConcurrency();
                        System.out.println(xx);
                    } catch (BusinessRuntimeException e) {
                        if (e.getErrorCode() == ErrorCode.REQUEST_BLOCK.getErrorCode()) {
                            existBlockException.set(true);
                        } else {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });

        }
        countDownLatch.await();
        mockWebServer.shutdown();
        executorService.shutdown();
        Assertions.assertTrue(existBlockException.get());
    }

}
