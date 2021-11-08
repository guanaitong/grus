/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by August.Zhou on 2021/9/2 12:50.
 */
public class IpUtilsTest {

    @Test
    public void testGetRequestIp() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn("1.1.1.1");
        String requestIp = IpUtils.getRequestIp(httpServletRequest);
        Assertions.assertEquals("1.1.1.1", requestIp);

        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn("1.1.1.2,1.1.1.3");
        requestIp = IpUtils.getRequestIp(httpServletRequest);
        Assertions.assertEquals("1.1.1.2",requestIp);

        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpServletRequest.getHeader("X-Real-IP")).thenReturn("1.1.1.4");
        requestIp = IpUtils.getRequestIp(httpServletRequest);
        Assertions.assertEquals("1.1.1.4",requestIp);

        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpServletRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(httpServletRequest.getRemoteAddr()).thenReturn("remote");
        requestIp = IpUtils.getRequestIp(httpServletRequest);
        Assertions.assertEquals("remote",requestIp);

        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn("unKnown");
        when(httpServletRequest.getHeader("X-Real-IP")).thenReturn("unKnown");
        when(httpServletRequest.getRemoteAddr()).thenReturn("remote");
        requestIp = IpUtils.getRequestIp(httpServletRequest);
        Assertions.assertEquals("remote",requestIp);
    }
}
