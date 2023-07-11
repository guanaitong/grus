/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import com.ciicgat.sdk.data.encryption.DbEncryptHelper;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August.Zhou on 2019-04-08 13:44.
 */

public class GrusFieldEncryptionAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(GrusFieldEncryptionAutoConfiguration.class);

    @Test
    public void test() {
        this.contextRunner
                .withPropertyValues("spring.application.name=grus-demo", "grus.db.field-encryption.secret-key=KRmAo5c6uavF7fYQ")
                .run(context -> {
                    DbEncryptHelper dbEncryptHelper = context.getBean(DbEncryptHelper.class);
                    assertThat(dbEncryptHelper).isInstanceOf(DbEncryptHelper.class);
                });
    }

}
