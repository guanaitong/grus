/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/3/27 16:04
 * @Description:
 */
public class UriUtils {
    public static boolean isExclude(String uri) {
        return uri.contains("isLive")
                || uri.endsWith(".css")
                || uri.endsWith(".js")
                || uri.endsWith(".ico")
                || uri.endsWith(".png")
                || uri.endsWith(".jpg");
    }
}
