/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.IgnoreError;
import com.ciicgat.api.core.annotation.ServiceName;
import feign.RequestLine;

import java.util.Optional;

/**
 * Created by August.Zhou on 2018-10-19 12:52.
 */
@ServiceName("error")
public interface ErrorService {

    @RequestLine("POST /getWithOutIgnoreError")
    Integer getWithOutIgnoreError();

    @RequestLine("POST /getWithIgnoreError")
    @IgnoreError
    Integer getWithIgnoreError();

    //以下测试各种原始类型和特殊类型返回

    @RequestLine("POST /getIntWithIgnoreError")
    @IgnoreError
    int getIntWithIgnoreError();

    @RequestLine("POST /getBoolenWithIgnoreError")
    @IgnoreError
    boolean getBoolenWithIgnoreError();

    @RequestLine("POST /getVoidWithIgnoreError")
    @IgnoreError
    void getVoidWithIgnoreError();

    @RequestLine("POST /getOptionalWithIgnoreError")
    @IgnoreError
    Optional<Integer> getOptionalWithIgnoreError();


    //retry
    @RequestLine("GET /getWith502Retry")
    Integer getWith502Retry();

}
