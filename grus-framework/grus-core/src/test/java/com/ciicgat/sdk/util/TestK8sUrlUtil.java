/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author wanchongyang
 * @date 2020/3/16 10:47 下午
 */
public class TestK8sUrlUtil {
    @Test
    public void test() {
        String url = "http://userdoor.services.dev.ofc/person/get";
        String convertUrl = K8sUrlUtil.convert(url);
        Assertions.assertSame(url, convertUrl);
    }
}
