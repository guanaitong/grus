/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.digest;

import com.ciicgat.sdk.lang.tool.SessionIdGenerator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by August.Zhou on 2023/4/17 17:49
 */
public class HMACSHA256 {

    /**
     * sha256_HMAC加密
     * @param secret  秘钥
     * @param message 消息
     * @return 加密后字符串
     */
    public static String encrypt(String secret, String message) {
        try {
            return encrypt(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"), message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(SecretKeySpec secret, String message) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            sha256_HMAC.init(secret);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String genKey() {
        return new SessionIdGenerator().generateSessionId(16);
    }
}
