/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.service;

import com.ciicgat.grus.boot.autoconfigure.core.GrusCoreContextInitializer;
import com.ciicgat.grus.service.GrusRuntimeContext;
import com.ciicgat.grus.service.GrusRuntimeManager;
import com.ciicgat.sdk.util.system.Systems;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August.Zhou on 2019-04-01 14:26.
 */
public class GrusServiceContextInitializerTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new GrusCoreContextInitializer());

    @Test
    public void test() {
        this.contextRunner
                .withPropertyValues("spring.application.name=grus-demo")
                .run(context -> {
                    System.out.println(Systems.APP_NAME);
                    assertThat("grus-demo".equals(Systems.APP_NAME)).isTrue();
                    GrusRuntimeManager grusRuntimeManager = context.getBean(GrusRuntimeManager.class);
                    assertThat(grusRuntimeManager).isInstanceOf(GrusRuntimeManager.class);
                    GrusRuntimeContext grusRuntimeContext = context.getBean(GrusRuntimeContext.class);
                    assertThat(grusRuntimeContext).isInstanceOf(GrusRuntimeContext.class);
                    assertThat(grusRuntimeContext.getAppName().equals("grus-demo")).isTrue();
                });
    }


}
