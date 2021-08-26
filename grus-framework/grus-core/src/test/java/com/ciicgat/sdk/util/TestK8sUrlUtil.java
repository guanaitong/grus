/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author wanchongyang
 * @date 2020/3/16 10:47 下午
 */
public class TestK8sUrlUtil {
    @Test
    public void test() {
        String url = "http://userdoor.services.dev.ofc/person/get";
        String convertUrl = K8sUrlUtil.convert(url);
        Assert.assertSame(url, convertUrl);
    }
}
