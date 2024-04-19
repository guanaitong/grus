/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.ciicgat.api.core.annotation.ServiceName;

@ServiceName("degrade-test-1")
public interface DegradeService1 {
    default void xx() {
    }

    void testDegradeOnMethod();

    void testDegradeOnClass();
}
