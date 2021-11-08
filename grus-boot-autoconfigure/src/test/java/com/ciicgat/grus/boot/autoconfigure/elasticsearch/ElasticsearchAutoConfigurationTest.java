/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.elasticsearch;

import com.ciicgat.grus.boot.autoconfigure.core.GrusCoreContextInitializer;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static com.ciicgat.grus.boot.autoconfigure.test.TestConstants.TEST_APP_PAIRS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August.Zhou on 2021/8/31 13:24.
 */
public class ElasticsearchAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new GrusCoreContextInitializer())
            .withUserConfiguration(ElasticsearchAutoConfiguration.class);

    @Test
    public void test1() {
        this.contextRunner
                .withPropertyValues(TEST_APP_PAIRS)
                .run(context -> {
                    Object restHighLevelClient = context.getBean("restHighLevelClient");
                    assertThat(restHighLevelClient).isInstanceOf(RestHighLevelClient.class);
                });
    }
}
