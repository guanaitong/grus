/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.digest;

import org.junit.Assert;
import org.junit.Test;

import java.util.Base64;
import java.util.Random;

/**
 * Created by August.Zhou on 2017/7/28 10:14.
 */
public class TestAESCrypt {

    @Test
    public void test() {
        String key = AESCrypt.init128Key();
        byte[] random = new byte[new Random().nextInt(1000)];
        new Random().nextBytes(random);
        String plainText = Base64.getEncoder().encodeToString(random);
        String text = AESCrypt.encode(key, plainText);
        Assert.assertEquals(plainText, AESCrypt.decode(key, text));
    }


    @Test
    public void test1() {
        String key = AESCrypt.init128Key();
        byte[] random = new byte[new Random().nextInt(1000)];
        new Random().nextBytes(random);
        byte[] text = AESCrypt.encode(key, random);
        Assert.assertArrayEquals(random, AESCrypt.decode(key, text));
    }

}
