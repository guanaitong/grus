/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.opentracing;

import com.ciicgat.grus.boot.autoconfigure.condition.ServerEnvCondition;
import com.ciicgat.grus.boot.autoconfigure.core.GrusCoreContextInitializer;
import io.opentracing.contrib.tracerresolver.TracerFactory;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August.Zhou on 2019-04-02 11:30.
 */
public class OpenTracingAutoConfigurationTests {

    static {
        ServerEnvCondition.isTest = true;
    }

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new GrusCoreContextInitializer())
            .withUserConfiguration(OpenTracingAutoConfiguration.class);

    @Test
    public void test() {
        this.contextRunner
                .withPropertyValues("spring.application.name=grus-demo")
                .run(context -> {
                    TracerFactory tracerFactory = context.getBean(TracerFactory.class);
                    assertThat(tracerFactory).isInstanceOf(GconfTracerFactory.class);
                });
    }


}
