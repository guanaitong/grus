/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.gfs;

import com.ciicgat.sdk.gfs.GfsClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static com.ciicgat.grus.boot.autoconfigure.test.TestConstants.TEST_APP_PAIRS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August.Zhou on 2021/8/31 13:37.
 */
public class GfsClientAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(GfsClientAutoConfiguration.class);

    @Test
    void gfsClient() {
        this.contextRunner
                .withPropertyValues(TEST_APP_PAIRS)
                .run(context -> {
                    Object gfsClient = context.getBean("gfsClient");
                    assertThat(gfsClient).isInstanceOf(GfsClient.class);
                });
    }
}
