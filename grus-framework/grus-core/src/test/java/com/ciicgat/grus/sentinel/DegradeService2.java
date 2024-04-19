/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.grus.sentinel.annotation.SimpleDegradeRule;
import com.ciicgat.grus.sentinel.annotation.SimpleFlowRule;

@ServiceName("degrade-test-2")
public interface DegradeService2 {
    @SimpleFlowRule(20)
    void testDegradeOnMethod();

    @SimpleDegradeRule(20)
    void testDegradeOnClass();
}
