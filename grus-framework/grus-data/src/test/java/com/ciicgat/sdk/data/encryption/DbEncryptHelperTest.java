/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.encryption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DbEncryptHelperTest {

    @Test
    void encrypt() {
        DbEncryptHelper dbEncryptHelper = new DbEncryptHelper("KRmAo5c6uavF7fYQ");
        String srcData = "86-13058102864";
        String expectedEncryptData = "5OVSHGWwefCu2QRMQbkzIT";
        //测试一次加密
        String encrypt01 = dbEncryptHelper.encrypt(srcData);
        assertEquals(encrypt01, expectedEncryptData);
        //二次加密, 密文不变
        String encrypt02 = dbEncryptHelper.encrypt(expectedEncryptData);
        assertEquals(encrypt02, expectedEncryptData);
    }

    @Test
    void decrypt() {
        DbEncryptHelper dbEncryptHelper = new DbEncryptHelper("KRmAo5c6uavF7fYQ");
        String data = "5OVSHGWwefCu2QRMQbkzIT";
        String expectedDecryptData = "86-13058102864";
        //测试一次解密
        String decrypt01 = dbEncryptHelper.decrypt(data);
        assertEquals(decrypt01, expectedDecryptData);
        //二次加密, 密文不变
        String decrypt02 = dbEncryptHelper.decrypt(expectedDecryptData);
        assertEquals(decrypt02, expectedDecryptData);

    }
}
