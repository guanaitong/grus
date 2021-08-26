/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.gconf;

import com.ciicgat.sdk.gconf.remote.RemoteConfigCollectionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @Author: Jiaju.Wei
 * @Date: Created in 2018/4/17
 * @Description:
 */
public class GconfBeanProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GconfBeanProcessor.class);

    public GconfBeanProcessor() {

    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        var gConfBeanAnnotation = bean.getClass().getAnnotation(GConfBean.class);
        if (gConfBeanAnnotation == null) {
            return bean;
        }

        AnnotationAttributes annotationAttributes = AnnotationUtils.getAnnotationAttributes(null, gConfBeanAnnotation);
        var configCollectionFactory = RemoteConfigCollectionFactoryBuilder.getInstance();
        String appId = annotationAttributes.getString("appId");
        String key = annotationAttributes.getString("key");
        boolean autoRefresh = annotationAttributes.getBoolean("autoRefresh");

        var configCollection = !appId.isEmpty() ?
                configCollectionFactory.getConfigCollection(appId) : configCollectionFactory.getConfigCollection();

        if (configCollection == null) {
            LOGGER.error("请设置appId或者应用名");
            throw new BeanInitializationException("请设置appId或者应用名");
        }

        try {
            bean = autoRefresh
                    ? configCollection.getBean(key, bean.getClass())
                    : configCollection.getLatestBean(key, bean.getClass());
        } catch (RuntimeException e) {
            throw new BeanInitializationException(gConfBeanAnnotation.key(), e);
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


}
