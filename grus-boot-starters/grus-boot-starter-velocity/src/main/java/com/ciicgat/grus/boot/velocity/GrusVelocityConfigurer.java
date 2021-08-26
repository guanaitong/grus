/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.velocity;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

/**
 * Created by August.Zhou on 2019-06-10 13:05.
 */
public class GrusVelocityConfigurer extends VelocityConfigurer {

    @Override
    protected void initSpringResourceLoader(VelocityEngine velocityEngine, String resourceLoaderPath) {
        velocityEngine.setProperty(
                RuntimeConstants.RESOURCE_LOADER, GrusSpringResourceLoader.NAME);
        velocityEngine.setProperty(
                GrusSpringResourceLoader.SPRING_RESOURCE_LOADER_CLASS, GrusSpringResourceLoader.class.getName());
        velocityEngine.setProperty(
                GrusSpringResourceLoader.SPRING_RESOURCE_LOADER_CACHE, "true");
        velocityEngine.setApplicationAttribute(
                GrusSpringResourceLoader.SPRING_RESOURCE_LOADER, getResourceLoader());
        velocityEngine.setApplicationAttribute(
                GrusSpringResourceLoader.SPRING_RESOURCE_LOADER_PATH, resourceLoaderPath);
    }

}
