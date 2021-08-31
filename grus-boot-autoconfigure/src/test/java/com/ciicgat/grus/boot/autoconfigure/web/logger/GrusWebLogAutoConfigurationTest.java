/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web.logger;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static com.ciicgat.grus.boot.autoconfigure.test.TestConstants.TEST_APP_PAIRS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August on 2021/8/31
 */
class GrusWebLogAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(GrusWebLogAutoConfiguration.class);
    @Test
    void grusWebLogPrinter() {
        this.contextRunner
                .withPropertyValues(TEST_APP_PAIRS)
                .run(context -> {
                    Object grusWebLogPrinter = context.getBean("grusWebLogPrinter");
                    assertThat(grusWebLogPrinter).isInstanceOf(GrusWebLogPrinter.class);
                });
    }
}
