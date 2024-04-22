/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.grus.service.none.Service3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Created by August.Zhou on 2024/4/22 09:48
 */
class ServicesTest {

    @Test
    void getServiceName() {
        ServiceName serviceName1 = Services.getServiceName(Service1.class);
        Assertions.assertEquals("services1", serviceName1.value());
        Assertions.assertEquals("xx", serviceName1.urlPathPrefix());

        ServiceName serviceName2 = Services.getServiceName(Service2.class);
        Assertions.assertEquals("service-package", serviceName2.value());

        Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Services.getServiceName(Service3.class);
            }
        });
    }

    @Test
    void isSecure() {
        Assertions.assertFalse(Services.isSecure(Service1.class));
        Services.isTest = true;
        Assertions.assertFalse(Services.isSecure(Service1.class));
        Assertions.assertTrue(Services.isSecure(Service2.class));
        Assertions.assertFalse(Services.isSecure(Service3.class));
    }

    @Test
    void getLogExclude() throws NoSuchMethodException {
        Assertions.assertNotNull(Services.getLogExclude(Service1.class.getDeclaredMethod("test1")));
        Assertions.assertNull(Services.getLogExclude(Service1.class.getDeclaredMethod("test2")));
        Assertions.assertNotNull(Services.getLogExclude(Service2.class.getDeclaredMethod("test1")));
    }
}
