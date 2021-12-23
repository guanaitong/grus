/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.opentelemetry;

import com.ciicgat.grus.boot.autoconfigure.condition.ServerEnvCondition;
import com.ciicgat.grus.boot.autoconfigure.core.GrusCoreContextInitializer;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August.Zhou on 2019-04-02 11:30.
 */
public class OpenTelemetryAutoConfigurationTests {

    static {
        ServerEnvCondition.isTest = true;
        GlobalOpenTelemetry.resetForTest();
    }

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new GrusCoreContextInitializer())
            .withUserConfiguration(OpenTelemetryAutoConfiguration.class);

    @Test
    public void test() {
        this.contextRunner
                .withPropertyValues("spring.application.name=grus-demo")
                .run(context -> {
                    OpenTelemetry tracerFactory = context.getBean(OpenTelemetry.class);
                    assertThat(tracerFactory).isInstanceOf(OpenTelemetry.class);
                });
    }


}
