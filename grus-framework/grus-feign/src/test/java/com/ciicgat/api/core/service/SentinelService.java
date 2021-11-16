/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ApiTimeout;
import com.ciicgat.api.core.annotation.ServiceName;
import feign.Param;
import feign.RequestLine;

/**
 * Created by August.Zhou on 2018-10-19 10:06.
 */
@ServiceName("sentinel-test")
public interface SentinelService {

    @RequestLine("POST /testFlow")
    @ApiTimeout(readTimeoutMillis = 100)
    void testFlow();

    @RequestLine("POST /testDegrade")
    @ApiTimeout(readTimeoutMillis = 100)
    void testDegrade();

    @RequestLine("POST /testAuthority")
    @ApiTimeout(readTimeoutMillis = 100)
    void testAuthority();

    @RequestLine("POST /testParamFlow?personId={personId}&id={id}")
    @ApiTimeout(readTimeoutMillis = 100)
    void testParamFlow(@Param("personId") Integer personId, @Param("id") Integer id);



}
