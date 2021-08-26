/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by August.Zhou on 2017/11/16 20:28.
 */
public class TestSessionIdGenerator {

    @Test
    public void test() {
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        Assert.assertEquals(32, sessionIdGenerator.generateSessionId().length());
        Assert.assertEquals(12, sessionIdGenerator.generateSessionId(6).length());

    }


}
