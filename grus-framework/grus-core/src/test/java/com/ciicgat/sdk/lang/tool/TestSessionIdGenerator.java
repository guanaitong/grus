/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2017/11/16 20:28.
 */
public class TestSessionIdGenerator {

    @Test
    public void test() {
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        Assertions.assertEquals(32, sessionIdGenerator.generateSessionId().length());
        Assertions.assertEquals(12, sessionIdGenerator.generateSessionId(6).length());

    }


}
