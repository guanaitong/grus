/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Created by August.Zhou on 2024/4/22 10:06
 */
class RequestIdProviderTest {

    @Test
    void getRequestId() {
        Assertions.assertNotNull(RequestIdProvider.NONE);
        Assertions.assertNull(RequestIdProvider.NONE.getRequestId(null));
    }
}
