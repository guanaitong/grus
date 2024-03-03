/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.service;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.grus.sentinel.annotation.SimpleFlowRule;
import feign.RequestLine;

/**
 * Created by HB1117 on 2023-11-1 12:52.
 */
@ServiceName("fallback")
public interface FallbackService {

    @RequestLine("GET /getWithOutFallback")
    @SimpleFlowRule(0)
    TestBean getWithOutFallback();
}
