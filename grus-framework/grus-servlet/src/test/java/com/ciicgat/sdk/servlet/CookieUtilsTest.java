/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.sdk.lang.tool.SessionIdGenerator;
import junit.framework.TestCase;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by August.Zhou on 2021/9/2 13:03.
 */
public class CookieUtilsTest extends TestCase {

    public void testGetCookieValue() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        when(httpServletRequest.getCookies()).thenReturn(null);
        String cookieValue3 = CookieUtils.getCookieValue("gid2", httpServletRequest);
        assertNull(cookieValue3);

        String value = sessionIdGenerator.generateSessionId();
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{new Cookie("gid", value)});
        String cookieValue = CookieUtils.getCookieValue("gid", httpServletRequest);
        assertEquals(cookieValue, value);

        String cookieValue2 = CookieUtils.getCookieValue("gid2", httpServletRequest);
        assertNull(cookieValue2);
    }
}
