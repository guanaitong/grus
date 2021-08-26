/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.digest;

import com.ciicgat.sdk.lang.tuple.KeyValuePair;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@SuppressWarnings("unused")
public class RSAKeyGenerator {
    public static final String KEY_ALGORITHM = "RSA";


    private static String encryptBASE64(byte[] key) {
        return Base64.getEncoder().encodeToString(key);
    }

    public static KeyValuePair<RSAPublicKey, RSAPrivateKey> initKey() {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new KeyValuePair<>(publicKey, privateKey);
    }

    public static KeyValuePair</*public*/String, /*private*/ String> initStringKey() {
        KeyValuePair<RSAPublicKey, RSAPrivateKey> keyValuePair = initKey();
        KeyValuePair<String, String> res = new KeyValuePair<>(
                encryptBASE64(keyValuePair.getKey().getEncoded()),
                encryptBASE64(keyValuePair.getValue().getEncoded()));
        return res;
    }
}
