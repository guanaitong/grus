/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.naming;

import com.ciicgat.api.core.EndPointConfigs;
import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.grus.service.ServiceNames;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by August.Zhou on 2023/10/31 14:54
 */
class NamingServiceTest {

    @Test
    void resolveBaseUrl() {
        NamingService namingService = NamingService.DEFAULT;
        ServiceName serviceName = ServiceNames.getServiceName(TestService.class);
        String s = namingService.resolveBaseUrl(serviceName);
        assertEquals("http://test-1.services.dev.ofc", s);

        EndPointConfigs.addEndPointConfig("test-1", "http://xxxx");
        String s2 = namingService.resolveBaseUrl(serviceName);
        assertEquals("http://xxxx", s2);

    }
}
