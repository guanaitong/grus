/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.notallowed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Created by August.Zhou on 2019-08-07 11:16.
 */
@Configuration(proxyBeanMethods = false)
public class GrusNotAllowedAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrusNotAllowedAutoConfiguration.class);


    public GrusNotAllowedAutoConfiguration() {
        // 正常情况下，该类不会加载，但是包扫描配置有问题的时候，会加载上。
        LOGGER.error("your component scan package cannot include com.ciicgat or start with com.ciicgat.grus");
        throw new UnsupportedOperationException("your component scan package cannot include com.ciicgat or start with com.ciicgat.grus");
    }
}
