/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util;

import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author wanchongyang
 * @date 2020/4/18 4:45 下午
 */
public class TestShortUrlUtil {
    @Test
    public void test() {
        String originUrl = "https://a.guanaitong.cc/festival/card-datails?greetingId=906";
        String shortUrl = ShortUrlUtil.create(originUrl);
        Assertions.assertNotNull(shortUrl);

        String reverseUrl = ShortUrlUtil.reverse(shortUrl);
        Assertions.assertEquals(originUrl, reverseUrl);
    }

    @Test
    public void testException() {
        String url = "中华人民共和国";
        Assertions.assertThrows(BusinessRuntimeException.class, () -> ShortUrlUtil.create(url));
    }
}
