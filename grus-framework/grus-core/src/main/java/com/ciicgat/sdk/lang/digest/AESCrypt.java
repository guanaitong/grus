/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.digest;


import com.ciicgat.sdk.lang.tool.Bytes;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by August.Zhou on 2016/2/19 15:52.
 */
public class AESCrypt {

    private static final String AESTYPE = "AES/ECB/PKCS5Padding";
    private static final char[] ARRAYS = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    public static String encode(final String secretKey, final String plainText) {
        return encode(Bytes.toBytes(secretKey), plainText);
    }

    public static String encode(final byte[] secretKey, final String plainText) {
        Key key = new SecretKeySpec(secretKey, "AES");
        try {
            Cipher cipher = Cipher.getInstance(AESTYPE); //NOSONAR
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedText = cipher.doFinal(Bytes.toBytes(plainText));
            return Base64.getEncoder().encodeToString(encryptedText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String decode(final String secretKey, final String cipherText) {
        return decode(Bytes.toBytes(secretKey), cipherText);
    }

    public static String decode(final byte[] secretKey, final String cipherText) {
        Key key = new SecretKeySpec(secretKey, "AES");
        try {
            Cipher cipher = Cipher.getInstance(AESTYPE); //NOSONAR
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] originBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return Bytes.toString(originBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encode(final String secretKey, final byte[] data) {
        Key key = new SecretKeySpec(Bytes.toBytes(secretKey), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(AESTYPE); //NOSONAR
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedText = cipher.doFinal(data);
            return encryptedText;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decode(final String secretKey, final byte[] cipherData) {
        Key key = new SecretKeySpec(Bytes.toBytes(secretKey), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(AESTYPE); //NOSONAR
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] originBytes = cipher.doFinal(cipherData);
            return originBytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    private static Key generateKey(String key) {
//        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
//        return keySpec;
//    }

    public static String init128Key() {
        //实例化
        StringBuilder stringBuilder = new StringBuilder(17);

        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        for (int i = 0; i < 16; i++) {
            stringBuilder.append(ARRAYS[threadLocalRandom.nextInt(ARRAYS.length)]);
        }
        return stringBuilder.toString();
    }


}
