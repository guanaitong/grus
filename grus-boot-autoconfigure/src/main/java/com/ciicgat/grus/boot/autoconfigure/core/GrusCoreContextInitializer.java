/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.core;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.boot.autoconfigure.constants.GrusConstants;
import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.ciicgat.grus.service.GrusFramework;
import com.ciicgat.grus.service.GrusRuntimeManager;
import com.ciicgat.grus.service.impl.StandardGrusRuntimeContext;
import com.ciicgat.grus.service.impl.StandardGrusRuntimeManager;
import com.ciicgat.sdk.gconf.GconfConfig;
import com.ciicgat.sdk.util.system.EnvHook;
import com.ciicgat.sdk.util.system.WorkRegion;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 主要负责初始化{@link com.ciicgat.grus.service.GrusRuntimeContext}
 *
 * @author August Zhou
 * @date 2019-03-06 10:02
 */
public class GrusCoreContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static AtomicBoolean isInitiated = new AtomicBoolean(false);

    private static GrusRuntimeManager grusRuntimeManager;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Class<?> mainClazz = deduceMainApplicationClass();
        if (mainClazz != null) {
            String packageName = mainClazz.getPackageName();
            // 用以保护com.ciicgat.grus的包不被作为componentScan的basePackages
            if ("com.ciicgat".equals(packageName)
                    || packageName.startsWith("com.ciicgat.grus")) {
                throw new UnsupportedOperationException("you cannot place you main class in package: " + packageName);
            }
        }
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        //先初始化从配置文件里初始化appName
        String appName = environment.getProperty(GrusConstants.APP_NAME_KEY);
        if (!StringUtils.hasText(appName)) {
            throw new RuntimeException("you should set spring.application.name");
        }
        String appNameFromEnv = System.getenv("APP_NAME");
        if (appNameFromEnv == null) {
            EnvHook.setAppName(appName);
        } else if (!appNameFromEnv.equals(appName)) { // 配置文件里的appName必须和环境变量里的一致
            throw new IllegalArgumentException("you should set spring.application.name as same as the env value of APP_NAME");
        }

        if (isInitiated.compareAndSet(false, true)) {
            StandardGrusRuntimeContext grusRuntimeContext = new StandardGrusRuntimeContext();
            grusRuntimeManager = new StandardGrusRuntimeManager(grusRuntimeContext);
            GrusFramework.setGrusRuntimeManager(grusRuntimeManager);
        }


        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton(GrusConstants.GRUS_RUNTIME_MANAGER_BEAN_ID, grusRuntimeManager);
        beanFactory.registerSingleton(GrusConstants.GRUS_RUNTIME_CONTEXT_BEAN_ID, grusRuntimeManager.getGrusRuntimeContext());

        initGlobalGconfConfig(appName);
        if (!WorkRegion.getCurrentWorkRegion().isDevelopOrTest()) {
            Alert.send(String.format("应用（%s）正在启动", appName));
        }
    }

    /**
     * 初始化grus通用配置
     */
    private void initGlobalGconfConfig(String appName) {
        GconfConfig.INSTANCE.setAppName(appName);
        GlobalGconfConfig.getConfig();
    }

    private Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        return null;
    }

}
