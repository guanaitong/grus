/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.DegradeService;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.sentinel.DegradeRuleOptions;
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
public class TestSentinelDegrade {
    static {
        SentinelRuleInit.doSth();
    }

    @Test
    public void testMethod() throws IOException, InterruptedException {
        DegradeRuleOptions grusDegradeRule = new DegradeRuleOptions();
        grusDegradeRule.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType())
                .setCount(200.0d)
                .setTimeWindow(10)
                .setMinRequestAmount(5)
                .setStatIntervalMs(1000)
                .setSlowRatioThreshold(0.5d);
        Pair<DegradeService, MockWebServer> pair = TestUtil.newInstance("degrade-test", new Supplier<DegradeService>() {
            @Override
            public DegradeService get() {
                DegradeService degradeService = FeignServiceBuilder.newBuilder().serviceClazz(DegradeService.class)
                        .options(new Request.Options(1000, 10_000))
                        .addMethodRule("testDegradeOnMethod", Set.of(grusDegradeRule)).build();
                return degradeService;
            }
        });
        MockWebServer mockWebServer = pair.getRight();
        DegradeService degradeService = pair.getLeft();

        TestBean bean1 = new TestBean("jasdlfj", 91823);
//        mockResponse响应时间是2s
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setHeadersDelay(2, TimeUnit.SECONDS)
                .setBody(JSON.toJSONString(bean1));
        for (int i = 0; i < 100; i++) {
            mockWebServer.enqueue(mockResponse);
        }
        final AtomicBoolean existBlockException = new AtomicBoolean(false);
//        必须启动一个线程池来调用接口，如果单线程调用，是不满足单位时间内最小请求数要求的
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int num = 20;
        CountDownLatch countDownLatch = new CountDownLatch(num);
        for (int i = 0; i < num; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TestBean xx = degradeService.testDegradeOnMethod();
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


    @Test
    public void testClass() throws IOException, InterruptedException {
        DegradeRuleOptions grusDegradeRule = new DegradeRuleOptions();
        grusDegradeRule.setCount(200.0d)
                .setTimeWindow(10)
                .setMinRequestAmount(5)
                .setStatIntervalMs(1000)
                .setSlowRatioThreshold(0.5d);
        Pair<DegradeService, MockWebServer> pair = TestUtil.newInstance("degrade-test", new Supplier<DegradeService>() {
            @Override
            public DegradeService get() {
                DegradeService degradeService = FeignServiceBuilder.newBuilder().serviceClazz(DegradeService.class)
                        .options(new Request.Options(1000, 10_000))
                        .classRules(Set.of(grusDegradeRule)).build();
                return degradeService;
            }
        });
        MockWebServer mockWebServer = pair.getRight();
        DegradeService degradeService = pair.getLeft();

        TestBean bean1 = new TestBean("jasdlfj", 91823);
//        mockResponse响应时间是2s
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .setResponseCode(200)
                .setHeader(HeaderConstants.ERROR_CODE_HEADER, 0)
                .setHeader(HeaderConstants.ERROR_MSG_HEADER, "")
                .setHeader(HeaderConstants.API_VERSION_HEADER, VersionConstants.V2)
                .setHeadersDelay(2, TimeUnit.SECONDS)
                .setBody(JSON.toJSONString(bean1));
        for (int i = 0; i < 100; i++) {
            mockWebServer.enqueue(mockResponse);
        }
        final AtomicBoolean existBlockException = new AtomicBoolean(false);
        //        必须启动一个线程池来调用接口，如果单线程调用，是不满足单位时间内最小请求数要求的
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int num = 20;
        CountDownLatch countDownLatch = new CountDownLatch(num);
        for (int i = 0; i < num; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TestBean xx = degradeService.testDegradeOnClass();
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
