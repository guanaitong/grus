/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.zk;

import com.ciicgat.grus.idgen.WorkIdHolder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static com.ciicgat.grus.boot.autoconfigure.test.TestConstants.TEST_APP_PAIRS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August on 2021/8/31
 */
class ZKAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(ZKAutoConfiguration.class);
    @Test
    void workIdHolder() {
        this.contextRunner
                .withPropertyValues(TEST_APP_PAIRS)
                .run(context -> {
                    Object workIdHolder = context.getBean("workIdHolder");
                    assertThat(workIdHolder).isInstanceOf(WorkIdHolder.class);
                });
    }
}
