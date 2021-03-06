/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.kubernetes;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @Author: August
 * @Date: 2021/7/15 23:09
 */
public class KubernetesClientConfigTest {

    @Test
    public void testGetConfig() {
        KubernetesClientConfig config = KubernetesClientConfig.getConfig();
        assertNotNull(config);
    }

    @Test
    public void testPrintDefault() {
        KubernetesClientConfig config = KubernetesClientConfig.DEFAULT;
        System.out.println(config);
    }
}
