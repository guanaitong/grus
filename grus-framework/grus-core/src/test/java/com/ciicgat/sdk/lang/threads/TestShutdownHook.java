/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.threads;

import org.junit.Test;

/**
 * Created by August.Zhou on 2018-10-22 13:47.
 */
public class TestShutdownHook {

    @Test(expected = IllegalArgumentException.class)
    public void test() {

        ShutdownHook.addShutdownHook(() -> System.out.println(System.currentTimeMillis()));

        ShutdownHook.addShutdownHook(new Thread(() -> System.out.println(System.currentTimeMillis())));
    }
}
