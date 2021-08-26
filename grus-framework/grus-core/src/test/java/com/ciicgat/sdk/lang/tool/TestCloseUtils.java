/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;

/**
 * Created by August.Zhou on 2018-10-22 14:13.
 */
public class TestCloseUtils {

    @Test
    public void test() {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[2]);
        CloseUtils.close(byteArrayInputStream);

        Object byteArrayInputStream1 = new ByteArrayInputStream(new byte[2]);
        CloseUtils.close(byteArrayInputStream1);

        CloseUtils.close(new Object());

        CloseUtils.close(null);
    }

    @Test
    public void test1() {
        CloseUtils.close(new Closeable() {
            @Override
            public void close() throws IOException {
                throw new RuntimeException("test crash");
            }
        });
    }
}
