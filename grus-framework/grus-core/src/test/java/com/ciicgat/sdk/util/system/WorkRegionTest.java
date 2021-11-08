/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Stanley Shen stanley.shen@guanaitong.com
 * @version 2020-11-12 14:42
 */
public class WorkRegionTest {

    /**
     * 构建时，是开发环境
     */
    @Test
    public void isTest() {
        Assertions.assertFalse(WorkRegion.getCurrentWorkRegion().isTest());
    }

    @Test
    public void isDevelop() {
        Assertions.assertTrue(WorkRegion.getCurrentWorkRegion().isDevelop());
    }

    @Test
    public void isDevelopOrTest() {
        Assertions.assertTrue(WorkRegion.getCurrentWorkRegion().isDevelopOrTest());
    }
}
