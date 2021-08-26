/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.kubernetes;

import junit.framework.TestCase;

/**
 * @Author: August
 * @Date: 2021/7/15 23:09
 */
public class KubernetesClientConfigTest extends TestCase {

    public void testGetConfig() {
        KubernetesClientConfig config = KubernetesClientConfig.getConfig();
        assertNotNull(config);
    }

    public void testPrintDefault() {
        KubernetesClientConfig config = KubernetesClientConfig.DEFAULT;
        System.out.println(config);
    }
}
