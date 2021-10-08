/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.CacheService;
import feign.Client;
import feign.Request;
import feign.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Jiaju.Wei
 * @Date: Created in 2018/6/19
 * @Description:
 */
public class TestCacheService {

    private static Client mockClient;

    private static Client mockClient1;


    @Before
    public void init() throws IOException {
        mockClient = Mockito.mock(Client.class);

        Request request = Request.create(Request.HttpMethod.GET, "", Collections.emptyMap(), (byte[]) null, null);
        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put(HeaderConstants.ERROR_CODE_HEADER, Collections.singletonList("0"));
        headers.put(HeaderConstants.ERROR_MSG_HEADER, Collections.singletonList(""));
        headers.put(HeaderConstants.API_VERSION_HEADER, Collections.singletonList(VersionConstants.V2));

        Response mockResponse = Response.builder().request(request).body("{}", Charset.defaultCharset()).status(200)
                .headers(headers).build();
        Mockito.when(mockClient.execute(Mockito.any(), Mockito.any())).thenReturn(mockResponse);


        mockClient1 = Mockito.mock(Client.class);
        Response mockResponse1 = Response.builder().request(request).body("", Charset.defaultCharset()).status(200).headers(new HashMap<>()).build();
        Mockito.when(mockClient1.execute(Mockito.any(), Mockito.any())).thenReturn(mockResponse1);

    }

    @After
    public void clear() {
        Mockito.reset(mockClient);
        Mockito.reset(mockClient1);

        FeignServiceFactory.removeCache(CacheService.class);
    }


    @Test
    public synchronized void testCached() throws Exception {
        TestBean result = new TestBean(null, 0);

        //第一次调用，没有缓存
        CacheService cacheService = FeignServiceFactory.newInstance(CacheService.class, mockClient);
        TestBean bean = cacheService.getBean();
        Assert.assertEquals(result, bean);

        //第二次调用，有缓存
        TestBean bean2 = cacheService.getBean();
        Assert.assertSame(bean2, bean);

        //客户端有且仅被调用一次
        Mockito.verify(mockClient, Mockito.times(1)).execute(Mockito.any(), Mockito.any());
    }

    @Test
    public synchronized void testCachedWithTwoParams() throws Exception {
        TestBean result = new TestBean(null, 0);
        CacheService cacheService = FeignServiceFactory.newInstance(CacheService.class, mockClient);
        //第一次调用，没有缓存
        TestBean bean = cacheService.getBeanWithTwoParams(1, 2);
        Assert.assertEquals(result, bean);

        //第二次调用，有缓存
        TestBean bean2 = cacheService.getBeanWithTwoParams(1, 2);
        Assert.assertSame(bean2, bean);

        //客户端有且仅被调用一次
        Mockito.verify(mockClient, Mockito.times(1)).execute(Mockito.any(), Mockito.any());
    }

    @Test
    public synchronized void testNotCached() throws Exception {

        CacheService cacheService = FeignServiceFactory.newInstance(CacheService.class, mockClient);
        //第一次调用，没有缓存
        cacheService.getBeanWithTwoParams(1, 2);

        //第二次调用，参数不同没有缓存
        cacheService.getBeanWithTwoParams(2, 3);

        //客户端被调用两次
        Mockito.verify(mockClient, Mockito.times(2)).execute(Mockito.any(), Mockito.any());
    }

    @Test
    public synchronized void testNull() throws Exception {

        CacheService cacheService = FeignServiceFactory.newInstance(CacheService.class, mockClient);
        TestBean result = new TestBean(null, 0);

        //第一次调用，没有缓存
        TestBean bean = cacheService.getBeanWithTwoParams(1, 2);
        Assert.assertEquals(result, bean);

        //第二次调用，有缓存
        TestBean bean2 = cacheService.getBeanWithTwoParams(1, 2);
        Assert.assertEquals(result, bean2);

        Mockito.verify(mockClient, Mockito.times(1)).execute(Mockito.any(), Mockito.any());
    }

    @Test
    public synchronized void testCacheNullValue() throws Exception {
        CacheService cacheService = FeignServiceFactory.newInstance(CacheService.class, mockClient1);

        //第一次调用，没有缓存
        TestBean bean = cacheService.getBeanWithTwoParams(1, 2);
        Assert.assertNull(bean);

        //第二次调用，有缓存
        TestBean bean2 = cacheService.getBeanWithTwoParams(1, 2);
        Assert.assertNull(bean2);

        Mockito.verify(mockClient1, Mockito.times(1)).execute(Mockito.any(), Mockito.any());
    }


    @Test
    public synchronized void testNotCacheNullValue() throws Exception {
        CacheService cacheService = FeignServiceFactory.newInstance(CacheService.class, mockClient1);
        //第一次调用，没有缓存
        TestBean bean = cacheService.getBeanNotCacheNullValue();
        Assert.assertNull(bean);
        //第二次调用，没有缓存
        TestBean bean2 = cacheService.getBeanNotCacheNullValue();
        Assert.assertNull(bean2);

        Mockito.verify(mockClient1, Mockito.times(2)).execute(Mockito.any(), Mockito.any());
    }


}
