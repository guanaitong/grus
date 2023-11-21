/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.trace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by August.Zhou on 2022/10/14 16:42
 */
class TraceUtilTest {

    @Test
    void removePrefixZeroChar() {
        assertEquals("40179b3ff3ca2a043579c206474898e", TraceUtil.removePrefixZeroChar("040179b3ff3ca2a043579c206474898e"));
        assertEquals("40179b3ff3ca2a043579c206474898e", TraceUtil.removePrefixZeroChar("00040179b3ff3ca2a043579c206474898e"));
        assertEquals("40179b3ff3ca2a043579c206474898e", TraceUtil.removePrefixZeroChar("00000000000000000000040179b3ff3ca2a043579c206474898e"));
    }
}
