/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

import com.ciicgat.grus.service.naming.NamingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2020-04-26 15:21.
 */
public class GconfEndPointNamingServiceTest {
    @Test
    public void test() {
        NamingService namingService = new GconfEndPointNamingService();
        Assertions.assertNotNull(namingService.resolve("kafka"));
    }
}
