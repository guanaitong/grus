/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util;

import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wanchongyang
 * @date 2020/4/18 4:45 下午
 */
public class TestShortUrlUtil {
    @Test
    public void test() {
        String originUrl = "https://a.guanaitong.cc/festival/card-datails?greetingId=906";
        String shortUrl = ShortUrlUtil.create(originUrl);
        Assert.assertNotNull(shortUrl);

        String reverseUrl = ShortUrlUtil.reverse(shortUrl);
        Assert.assertEquals(originUrl, reverseUrl);
    }

    @Test(expected = BusinessRuntimeException.class)
    public void testException() {
        String url = "中华人民共和国";
        ShortUrlUtil.create(url);
    }
}
