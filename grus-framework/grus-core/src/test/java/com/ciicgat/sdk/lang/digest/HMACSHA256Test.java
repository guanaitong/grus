package com.ciicgat.sdk.lang.digest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by August.Zhou on 2023/4/17 18:03
 */
class HMACSHA256Test {

    @Test
    void encrypt() {
        String key = HMACSHA256.genKey();
        assertEquals(32, key.length());
        assertEquals("bAnfa7Wvtu6tm/V7gLr4bqhPKAblC++Sw5etx0l7EVw=", HMACSHA256.encrypt("659D2D09F1EBFA76E101FB588447E1C9", "123aaa111"));

        assertEquals("zGnV2r2UvUX3f999ixFYD9h/atb8TwvYijIslwC75Yo=", HMACSHA256.encrypt("659D2D09F1EBFA76E101FB588447E1C911", "123aaa111111"));

    }


}