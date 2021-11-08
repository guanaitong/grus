/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.digest;

import com.ciicgat.sdk.lang.tuple.KeyValuePair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2017/7/28 10:14.
 */
public class TestRSACrypt {

    @Test
    public void test() throws Exception {
        byte[] data = "1123asdf&!@#$%^&*()_+~`我的啊谁都快放假的撒噶jfasd;lkjfl;dksajglkjhdaslkjglkdsajg".getBytes();
        KeyValuePair<String, String> keyValuePair = RSAKeyGenerator.initStringKey();
        byte[] bytes = RSACrypt.encryptByPublicKey(data, keyValuePair.getKey());
        Assertions.assertArrayEquals(data, RSACrypt.decryptByPrivateKey(bytes, keyValuePair.getValue()));
    }


    @Test
    public void test1() throws Exception {
        byte[] data = "1123asdf&!@#$%^&*()_+~`我的啊谁都快放假的撒噶jfasd;lkjfl;dksajglkjhdaslkjglkdsajg".getBytes();
        KeyValuePair<String, String> keyValuePair = RSAKeyGenerator.initStringKey();
        byte[] bytes = RSACrypt.encryptByPrivateKey(data, keyValuePair.getValue());
        Assertions.assertArrayEquals(data, RSACrypt.decryptByPublicKey(bytes, keyValuePair.getKey()));
    }

}
