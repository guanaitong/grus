/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Created by August.Zhou on 2024/4/19 21:17
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = IntegrationApplication.class,
        properties = {"spring.application.name=grus-demo",
                "grus.hibernate.validator.enabled=true",
                "grus.web.http2.enabled=true",
                "grus.web.http2.clientAuth=true",
                "grus.validation.errorCode=100"})
public class IntegrationTest {
    @Test
    public void test() {

    }
}
