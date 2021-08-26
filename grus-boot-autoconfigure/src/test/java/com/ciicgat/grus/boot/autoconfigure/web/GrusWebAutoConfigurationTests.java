/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web;

import com.ciicgat.grus.boot.autoconfigure.core.GrusCoreContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Created by August.Zhou on 2019-04-02 11:35.
 */
public class GrusWebAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new GrusCoreContextInitializer())
            .withUserConfiguration(GrusWebAutoConfiguration.class);

//    @Test
//    public void test() {
//        this.contextRunner
//                .withPropertyValues("spring.application.name=grus-demo")
//                .run(context -> {
//                    CircuitBreakerFilter circuitBreakerFilter = context.getBean(CircuitBreakerFilter.class);
//                    assertThat(circuitBreakerFilter).isInstanceOf(CircuitBreakerFilter.class);
//                });
//    }

}
