/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.system;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertFalse(WorkRegion.getCurrentWorkRegion().isTest());
    }

    @Test
    public void isDevelop() {
        Assert.assertTrue(WorkRegion.getCurrentWorkRegion().isDevelop());
    }

    @Test
    public void isDevelopOrTest() {
        Assert.assertTrue(WorkRegion.getCurrentWorkRegion().isDevelopOrTest());
    }
}
