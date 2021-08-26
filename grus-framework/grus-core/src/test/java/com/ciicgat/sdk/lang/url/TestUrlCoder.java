/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.url;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2018-10-19 17:41.
 */
public class TestUrlCoder {

    @Test
    public void testEnDe() {
        String raw = "中华人民共和国万岁,abcd";
        String encode = UrlCoder.encode(raw);
        Assert.assertEquals(UrlCoder.decode(encode), raw);
        Assert.assertEquals("%E4%B8%AD%E5%8D%8E%E4%BA%BA%E6%B0%91%E5%85%B1%E5%92%8C%E5%9B%BD%E4%B8%87%E5%B2%81%2Cabcd", encode);
    }

    @Test
    public void testBuildUrl() {
        String url = "http://www.baidu.com";
        Map<String, String> params = new HashMap<>();

        params.put("name", UrlCoder.encode("孙长浩"));
        String u = UrlCoder.build(url, params);
        Assert.assertEquals("http://www.baidu.com?name=%E5%AD%99%E9%95%BF%E6%B5%A9", u);
    }


}
