/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.interceptor;

import java.lang.reflect.Method;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/11/21 10:47
 * @Description:
 */
public interface GHandlerInterceptor {
    void preHandle(Object proxy, Method method, Object[] args, String serviceName);
}
