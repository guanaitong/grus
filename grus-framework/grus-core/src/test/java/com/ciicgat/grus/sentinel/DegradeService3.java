/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.ciicgat.api.core.annotation.ServiceName;

@ServiceName("degrade-test-3")
public interface DegradeService3 {
    void testDegradeOnMethod();

    void testDegradeOnClass();
}
