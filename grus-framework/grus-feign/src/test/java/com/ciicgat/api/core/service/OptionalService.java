/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.api.core.model.TestBean;
import feign.RequestLine;

import java.util.Optional;

/**
 * Created by August.Zhou on 2018-10-30 13:18.
 */
@ServiceName("optional")
public interface OptionalService {

    @RequestLine("GET /get")
    Optional<TestBean> get();

    @RequestLine("GET /getRespWithDataNull")
    Optional<TestBean> getRespWithDataNull();

    @RequestLine("GET /getRespWithWrongHttpStatus")
    Optional<TestBean> getRespWithWrongHttpStatus();
}
