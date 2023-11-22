/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;


import jakarta.servlet.http.HttpServletRequest;

/**
 * Created by August.Zhou on 2023/11/22 15:43
 */
public interface RequestIdProvider {

    String getRequestId(HttpServletRequest httpServletRequest);

    RequestIdProvider NONE = httpServletRequest -> null;
}
