/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.swagger;

import com.ciicgat.grus.boot.autoconfigure.core.GrusCoreContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Created by August.Zhou on 2019-04-02 11:35.
 */
public class SwaggerAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new GrusCoreContextInitializer())
            .withUserConfiguration(SwaggerAutoConfiguration.class);

//    @Test
//    public void test() {
//        this.contextRunner
//                .withPropertyValues("spring.application.name=grus-demo")
//                .run(context -> {
//                    Object docket = context.getBean(Docket.class);
//                    assertThat(docket).isInstanceOf(Docket.class);
//
//                });
//    }
}
