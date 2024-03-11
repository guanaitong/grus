/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.contants.HeaderConstants;
import com.ciicgat.api.core.contants.VersionConstants;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.FallbackService;
import feign.Client;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: HB1117
 * @date: 2023.11.01
 */
public class TestFallback {

    private Client mockClient;
    private static FallbackService fallbackService;

    @BeforeEach
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
    }

    @AfterEach
    public void clear() {
        Mockito.reset(mockClient);
    }

    @Test
    public void testFallback() throws IOException {
        TestFallBackFactory fallback = new TestFallBackFactory();
        fallbackService = FeignServiceFactory.newInstance(FallbackService.class, mockClient, fallback);
        TestBean testBean = fallbackService.getWithOutFallback();
        Assertions.assertEquals(testBean.getInteger(), 11);
    }
}
