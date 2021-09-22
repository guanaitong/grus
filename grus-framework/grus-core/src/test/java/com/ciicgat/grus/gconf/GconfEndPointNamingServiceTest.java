/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.gconf;

import com.ciicgat.grus.service.naming.NamingService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by August.Zhou on 2020-04-26 15:21.
 */
public class GconfEndPointNamingServiceTest {
    @Test
    public void test() {
        NamingService namingService = new GconfEndPointNamingService();
        Assert.assertNotNull(namingService.resolve("kafka"));
    }
}
