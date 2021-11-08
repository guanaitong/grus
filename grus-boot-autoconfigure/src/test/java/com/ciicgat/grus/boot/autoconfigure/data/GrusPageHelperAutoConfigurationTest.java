/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import com.ciicgat.grus.boot.autoconfigure.core.GrusCoreContextInitializer;
import com.github.pagehelper.PageInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static com.ciicgat.grus.boot.autoconfigure.test.TestConstants.TEST_APP_PAIRS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August.Zhou on 2021/8/31 13:33.
 */
public class GrusPageHelperAutoConfigurationTest {

    @Test
    public void testPageHelperConfiguration() {
        var contextRunner = new ApplicationContextRunner()
                .withInitializer(new GrusCoreContextInitializer())
                .withUserConfiguration(GrusDataAutoConfiguration.class, GrusMybatisAutoConfiguration.class, GrusPageHelperAutoConfiguration.class);
        contextRunner
                .withPropertyValues(TEST_APP_PAIRS, "grus.pagehelper.enable=true", "grus.pagehelper.offsetAsPageNum=true")
                .run(context -> {
                    Object pageInterceptor = context.getBean("pageInterceptor");
                    assertThat(pageInterceptor).isInstanceOf(PageInterceptor.class);
                });
    }

}
