/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import com.ciicgat.sdk.lang.url.UrlCoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Created by August.Zhou on 2017/6/23 13:01.
 */
public class CookieUtils {

    public static String getCookieValue(String cookieName, HttpServletRequest httpServletRequest) {
        Objects.requireNonNull(cookieName);
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return UrlCoder.decode(cookie.getValue());
            }
        }
        return null;
    }


}
