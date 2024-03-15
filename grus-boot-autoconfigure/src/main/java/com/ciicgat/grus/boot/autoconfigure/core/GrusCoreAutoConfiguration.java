/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.core;

import com.ciicgat.grus.idgen.IdGenerator;
import com.ciicgat.grus.idgen.SnowflakeIdGenerator;
import com.ciicgat.grus.idgen.WorkIdHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by August.Zhou on 2019-04-12 9:51.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({CoreProperties.class, IdGenProperties.class})
public class GrusCoreAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(name = "idGenerator")
    @ConditionalOnBean(name = "workIdHolder")
    public IdGenerator idGenerator(WorkIdHolder workIdHolder, IdGenProperties idGenProperties) {
        return new SnowflakeIdGenerator(workIdHolder, idGenProperties.getDateFormat());
    }

}
