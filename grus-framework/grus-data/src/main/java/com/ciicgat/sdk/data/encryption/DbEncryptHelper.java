/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.encryption;

import com.ciicgat.sdk.lang.digest.AESCrypt;
import com.ciicgat.sdk.lang.tool.Bytes;
import com.ciicgat.sdk.util.base.Base62;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class DbEncryptHelper {
    private static final Base62 BASE_62 = Base62.createInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(DbEncryptHelper.class);

    private final String secretKey;

    public DbEncryptHelper(String secretKey) {
        this.secretKey = secretKey;
    }

    public String encrypt(String data) {
        if (null == data) {
            return null;
        }
        if ("".equals(data)) {
            return "";
        }

        if (isEncryptByte(data)) {
            LOGGER.info("数据已经Base62加密，不再进行二次加密");
            return data;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("try to encrypt");
        }
        byte[] s = AESCrypt.encode(secretKey, data.getBytes());
        return new String(BASE_62.encode(s));
    }

    public String decrypt(String data) {
        if (null == data) {
            return null;
        }
        if ("".equals(data)) {
            return "";
        }
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("try to decrypt");
            }
            byte[] decode1 = BASE_62.decode(data.getBytes());
            return Bytes.toString(AESCrypt.decode(secretKey, decode1));
        } catch (Exception e) {
            LOGGER.info("解密失败，有可能不是Base62的字符，不需要解密:{}", data);
            return data;
        }
    }

    public Base62 getBase62() {
        return BASE_62;
    }

    public boolean isEncryptByte(String data) {
        if (!BASE_62.isBase62Encoding(data.getBytes())) {
            return false;
        }
        //只要是纯数字、纯小写字母或纯大学字母，就不是BASE62格式的数据
        if (validateIsNotBase62(data)) {
            return false;
        }

        byte[] decode1 = BASE_62.decode(data.getBytes());
        try {
            AESCrypt.decode(secretKey, decode1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static final String BASE_62_1 = "^[0-9]*$";
    private static final String BASE_62_2 = "^[a-z]*$";
    private static final String BASE_62_3 = "^[A-Z]*$";
    private static final Pattern BASE_62_1_PATTERN = Pattern.compile(BASE_62_1);
    private static final Pattern BASE_62_2_PATTERN = Pattern.compile(BASE_62_2);
    private static final Pattern BASE_62_3_PATTERN = Pattern.compile(BASE_62_3);

    private static boolean validateIsNotBase62(String content) {
        return BASE_62_1_PATTERN.matcher(content).find() || BASE_62_2_PATTERN.matcher(content).find() || BASE_62_3_PATTERN.matcher(content).find();
    }

}
