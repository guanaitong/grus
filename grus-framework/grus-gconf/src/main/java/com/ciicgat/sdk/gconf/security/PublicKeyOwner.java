/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.security;

import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.lang.digest.RSACrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author August
 * @date 2020/2/26 10:50 AM
 */
public class PublicKeyOwner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicKeyOwner.class);
    public static final String PUBLIC_KEY;

    static {
        try {
            PUBLIC_KEY = GlobalGconfConfig.getConfig().getConfig("publicKey");
        } catch (Exception e) {
            LOGGER.info("init public key failed", e);
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encryptedPwd) {
        byte[] bytes = Base64.getDecoder().decode(encryptedPwd);
        try {
            return new String(RSACrypt.decryptByPublicKey(bytes, PUBLIC_KEY), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("密码错误，无法解密:" + encryptedPwd, e);
            throw new RuntimeException(e);
        }
    }
}
