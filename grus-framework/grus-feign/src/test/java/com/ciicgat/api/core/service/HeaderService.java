/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.api.core.model.TestBean;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Headers("Content-Type: application/x-www-form-urlencoded")
@ServiceName("get-delete")
public interface HeaderService {

    @Headers({"Content-Type: application/json"})
    @RequestLine("POST /get/{serverId}")
    TestBean get(@Param("serverId") String serverId);

    @RequestLine("POST /get/default/{serverId}")
    TestBean getDefault(@Param("serverId") String serverId);

}
