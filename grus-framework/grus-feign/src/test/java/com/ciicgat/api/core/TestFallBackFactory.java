/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.model.TestBean;
import com.ciicgat.api.core.service.FallbackService;

/**
 * @Description:
 * @author: HB1117
 * @date: 2023.11.01
 */
public class TestFallBackFactory implements FallbackFactory<FallbackService> {

    @Override
    public FallbackService create(Throwable cause) {
        FallbackService fallbackService = new FallbackService() {
            @Override
            public TestBean getWithOutFallback() {
                return new TestBean("I'm fallback bean", 11);
            }
        };
        return fallbackService;
    }
}
