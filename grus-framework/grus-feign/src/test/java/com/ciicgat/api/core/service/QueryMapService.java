/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ServiceName;
import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

/**
 * QueryMap中的参数只会被框架加载到URL里作为查询参数。所以改注解对于POST URL FORM请求类型来说，并不会把其中的参数加载到表单的参数里。
 * <p>
 * Created by August.Zhou on 2017/8/31 10:31.
 */
@ServiceName("querymap")
@Headers("Content-Type: application/x-www-form-urlencoded")
public interface QueryMapService {

    @RequestLine("GET /send?parameterName={parameterName}")
    Boolean get(
            @Param("parameterName") String parameterName,
            @QueryMap Map<String, Object> otherParams);


    @RequestLine("POST /send")
    Boolean post(
            @Param("parameterName") String parameterName,
            @QueryMap Map<String, Object> otherParams);
}
