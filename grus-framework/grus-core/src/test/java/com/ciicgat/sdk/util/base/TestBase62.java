/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.base;

import com.ciicgat.sdk.lang.digest.AESCrypt;
import com.ciicgat.sdk.lang.tool.Bytes;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class TestBase62 {
    private static final String SECRET_KEY = "xlIPtbAJxOOkJsHW";

    @Test
    public void testGMP() {
        for (int i = 0; i < 20000; i++) {
            int memberId = new Random().nextInt();

            byte[] s = AESCrypt.encode(SECRET_KEY, Bytes.toBytes(memberId));
            Base62 base62Util = Base62.createInstance();
            String x = new String(base62Util.encode(s));
            byte[] decode1 = base62Util.decode(x.getBytes());
            int decode = Bytes.toInt(AESCrypt.decode(SECRET_KEY, decode1));
            if (decode != memberId) {
                throw new RuntimeException("不匹配停止掉");
            }

        }
    }

    @Test
    public void testINVERTED() {
        for (int i = 0; i < 20000; i++) {
            int memberId = new Random().nextInt();

            byte[] s = AESCrypt.encode(SECRET_KEY, Bytes.toBytes(memberId));
            Base62 base62Util = Base62.createInstanceWithInvertedCharacterSet();
            String x = new String(base62Util.encode(s));
            byte[] decode1 = base62Util.decode(x.getBytes());
            int decode = Bytes.toInt(AESCrypt.decode(SECRET_KEY, decode1));
            if (decode != memberId) {
                throw new RuntimeException("不匹配停止掉");
            }

        }
    }
}
