/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ServiceName;
import feign.Headers;
import feign.RequestLine;

/**
 * Created by August on 2021/9/15
 */
@ServiceName("retry")
public interface RetryService {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestLine("POST /get")
    int get();
}
