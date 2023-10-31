/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.webflux.client;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.webflux.client.service.Region;
import com.ciicgat.grus.webflux.client.service.RegionService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

/**
 * Created by August.Zhou on 2023/10/31 10:18
 */
public class TestRegionService {

    private static MockWebServer mockWebServer;
    private static RegionService testService;

    @BeforeAll
    public static void init() {
        Pair<RegionService, MockWebServer> pair = TestUtil.newInstance("region-1", RegionService.class);
        mockWebServer = pair.getRight();
        testService = pair.getLeft();
    }

    @AfterAll
    public static void stop() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    public synchronized void testGet() {
        Region bean = new Region(3, "sh", "prod", 123);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(JSON.toJSONString(bean));
        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);

        Region bean1 = testService.getRegionById(3);
        Assertions.assertEquals(bean, bean1);
        Mono<Region> bean2 = testService.getRegionById1(3);
        Assertions.assertEquals(bean, bean2.block());
    }

    @Test
    public synchronized void testList() {
        Region bean = new Region(3, "sh", "prod", 123);
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(JSON.toJSONString(List.of(bean)));
        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);

        List<Region> bean1 = testService.getAllRegion();
        Assertions.assertEquals(List.of(bean), bean1);
        Flux<Region> bean2 = testService.getAllRegion2();
        Assertions.assertEquals(List.of(bean), bean2.collectList().block());
    }
}
