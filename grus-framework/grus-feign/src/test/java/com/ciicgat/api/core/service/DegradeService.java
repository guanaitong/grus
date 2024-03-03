/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.api.core.model.TestBean;
import feign.Headers;
import feign.RequestLine;

@ServiceName("degrade-test")
@Headers("Content-Type: application/x-www-form-urlencoded")
public interface DegradeService {
    @RequestLine("POST /testDegradeOnMethod")
    TestBean testDegradeOnMethod();

    @RequestLine("POST /testDegradeOnMethod")
    TestBean testDegradeOnClass();
}
