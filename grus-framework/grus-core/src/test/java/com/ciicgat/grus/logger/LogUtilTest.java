/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.logger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by August.Zhou on 2022/10/11 13:44
 */
class LogUtilTest {

    @Test
    void removePrefixZeroChar() {
        assertEquals("40179b3ff3ca2a043579c206474898e", LogUtil.removePrefixZeroChar("040179b3ff3ca2a043579c206474898e"));
        assertEquals("40179b3ff3ca2a043579c206474898e", LogUtil.removePrefixZeroChar("00040179b3ff3ca2a043579c206474898e"));
        assertEquals("40179b3ff3ca2a043579c206474898e", LogUtil.removePrefixZeroChar("00000000000000000000040179b3ff3ca2a043579c206474898e"));
    }
}