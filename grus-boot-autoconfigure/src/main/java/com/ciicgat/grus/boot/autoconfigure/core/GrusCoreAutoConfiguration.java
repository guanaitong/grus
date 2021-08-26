/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by August.Zhou on 2019-04-12 9:51.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CoreProperties.class)
public class GrusCoreAutoConfiguration {


}
