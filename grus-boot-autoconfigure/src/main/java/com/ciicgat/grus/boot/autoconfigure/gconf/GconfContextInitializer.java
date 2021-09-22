/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.gconf;

import com.ciicgat.grus.boot.autoconfigure.constants.GrusConstants;
import com.ciicgat.grus.service.GrusFramework;
import com.ciicgat.grus.service.GrusRuntimeManager;
import com.ciicgat.grus.service.impl.StandardGrusRuntimeManager;
import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import com.ciicgat.sdk.lang.tool.PropertiesUtils;
import com.ciicgat.sdk.util.ComponentStatus;
import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 该类的主要目的，在于把properties加载到PropertySources里，支持@Value这种写法。同时注册 {@GconfBeanProcessor}
 * <p>
 * Created by August.Zhou on 2019-03-04 17:03.
 */
public class GconfContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GconfContextInitializer.class);

    private static final String LOCAL_PROPERTIES_NAME = "local.properties";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        var environment = applicationContext.getEnvironment();

        // 本地配置加载逻辑
        if (System.getenv(GrusConstants.WORK_ENV) == null && System.getenv(GrusConstants.WORK_IDC) == null) {
            ClassPathResource classPathResource = new ClassPathResource(LOCAL_PROPERTIES_NAME);
            if (classPathResource.exists()) {
                try (InputStream is = classPathResource.getInputStream()) {
                    Properties properties = new Properties();
                    properties.load(is);
                    if (!properties.isEmpty()) {
                        LOGGER.info("注入本地配置 {}", LOCAL_PROPERTIES_NAME);
                        PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource(LOCAL_PROPERTIES_NAME, properties);
                        // 从local.properties中获取的配置，优先级最高
                        environment.getPropertySources().addFirst(propertiesPropertySource);
                    }
                } catch (IOException e) {
                    // no-op
                }
            }
        }

        applicationContext.getBeanFactory().addBeanPostProcessor(new GconfBeanProcessor());


        var configCollection = RemoteConfigCollectionFactoryBuilder.getInstance().getConfigCollection();

        if (configCollection == null) {
            LOGGER.warn("gconf configApp {} 不存在,可能影响一些框架的自动加载", Systems.APP_NAME);
            return;
        }

        GrusRuntimeManager grusRuntimeManager = GrusFramework.getGrusRuntimeManager();
        ((StandardGrusRuntimeManager) grusRuntimeManager).setGrusRuntimeConfig(new GconfGrusRuntimeConfig());


        Map<String, String> map = configCollection.asMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().endsWith(PropertiesUtils.SUFFIX)) {
                Properties properties = PropertiesUtils.readFromText(entry.getValue());
                if (!properties.isEmpty()) {
                    LOGGER.info("注入配置 {}", entry.getKey());
                    PropertiesPropertySource propertySource = new PropertiesPropertySource(entry.getKey(), properties);
                    //从gconf中获取的配置，优先级最低
                    environment.getPropertySources().addLast(propertySource);
                }
            } else {
                LOGGER.info("跳过注入配置{}, 非properties文件请使用gconf api获取配置值", entry.getKey());
            }
        }
        initSwagger(environment);
    }

    /**
     * 读取完所有配置后，根据 grus.swagger.enabled 和环境，判断是否启用 swagger
     *
     * @param environment spring.environment
     */
    private void initSwagger(ConfigurableEnvironment environment) {
        // 根据环境和配置，判断是否启用 swagger
        boolean enabledEnv = WorkRegion.getCurrentWorkRegion().isDevelopOrTest();
        Boolean grusSwaggerEnabled = environment.getProperty("grus.swagger.enabled", Boolean.class, Boolean.TRUE);
        boolean enableSwagger = enabledEnv && grusSwaggerEnabled.booleanValue();

        Map<String, Object> paramMap = Map.of("springfox.documentation.enabled", enableSwagger, "grus.swagger.enabled", enableSwagger);
        PropertySource<Map<String, Object>> swaggerProperties = new MapPropertySource("swaggerProperties", paramMap);
        // 读取 grus.swagger.enabled，覆盖 springfox 的 enabled 配置
        environment.getPropertySources().addFirst(swaggerProperties);
    }

}
