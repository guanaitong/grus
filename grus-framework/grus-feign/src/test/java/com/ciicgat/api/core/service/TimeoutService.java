/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ApiTimeout;
import com.ciicgat.api.core.annotation.ServiceName;
import feign.Headers;
import feign.RequestLine;

/**
 * Created by August.Zhou on 2018-10-19 10:06.
 */
@ServiceName("timeout")
@ApiTimeout(readTimeoutMillis = 3000)
public interface TimeoutService {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestLine("POST /get")
    int get();

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestLine("POST /getWithApiTimeoutAnnotation")
    @ApiTimeout(readTimeoutMillis = 1000)
    int getWithApiTimeoutAnnotation();


}
