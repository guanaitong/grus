/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.frigate;

import com.ciicgat.grus.performance.SlowLogger;
import com.ciicgat.grus.service.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 因为frigate没办法使用Gconf，所以采用了本地文件的配置方法
 * Created by August.Zhou on 2019-02-22 10:51.
 */
public class FrigateEnvironmentCustomizer implements EnvironmentPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrigateEnvironmentCustomizer.class);


    static {
        NamingService.DEFAULT.addFirst(new FrigateNamingService());
        SlowLogger.closeSendSlowLog();
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
//        final String WORK_ENV = System.getenv("WORK_ENV") == null ? "local" : System.getenv("WORK_ENV");
//
//        LOGGER.info("WORK_ENV {}", WORK_ENV);
//
//        ClassPathResource classPathResource = new ClassPathResource(WORK_ENV + "/config.properties");
//        Properties configuration = new Properties();
//        try {
//            configuration.load(classPathResource.getInputStream());
//        } catch (IOException e) {
//            LOGGER.error("error", e);
//            throw new RuntimeException(e);
//        }
//        PropertiesPropertySource propertySource = new PropertiesPropertySource(
//                "config.properties", configuration);
//        environment.getPropertySources().addLast(propertySource);
    }
}
