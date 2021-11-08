/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * Created by August.Zhou on 2018-10-22 15:57.
 */
public class TestBytes {

    @Test
    public void testString() {
        SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
        String text = sessionIdGenerator.generateSessionId(200);
        byte[] bytes = Bytes.toBytes(text);
        Assertions.assertEquals(text, Bytes.toString(bytes));
    }

    @Test
    public void testBoolean() {
        boolean b = new Random().nextBoolean();
        byte[] bytes = Bytes.toBytes(b);
        Assertions.assertEquals(b, Bytes.toBoolean(bytes));
    }

    @Test
    public void testShort() {
        short b = (short) new Random().nextLong();
        byte[] bytes = Bytes.toBytes(b);
        Assertions.assertEquals(b, Bytes.toShort(bytes));
    }

    @Test
    public void testInt() {
        int b = new Random().nextInt();
        byte[] bytes = Bytes.toBytes(b);
        Assertions.assertEquals(b, Bytes.toInt(bytes));
    }

    @Test
    public void testLong() {
        long b = new Random().nextLong();
        byte[] bytes = Bytes.toBytes(b);
        Assertions.assertEquals(b, Bytes.toLong(bytes));
    }

    @Test
    public void testFloat() {
        float b = new Random().nextFloat();
        byte[] bytes = Bytes.toBytes(b);
        Assertions.assertEquals(b, Bytes.toFloat(bytes), 0.001d);
    }

    @Test
    public void testDouble() {
        double b = new Random().nextDouble();
        byte[] bytes = Bytes.toBytes(b);
        Assertions.assertEquals(b, Bytes.toDouble(bytes), 0.001d);
    }
}
