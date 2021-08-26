/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util;

import com.ciicgat.sdk.lang.tool.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author August.Zhou
 * @date 2019-04-26 10:07
 */
public class ComponentStatus {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentStatus.class);
    private static final boolean TRACE_CLASS_PRESENT =
            ClassUtils.isPresent("io.opentracing.Tracer")
                    && ClassUtils.isPresent("io.jaegertracing.internal.JaegerTracer")
                    && ClassUtils.isPresent("com.ciicgat.sdk.trace.Spans");

    private static final boolean GCONF_CLASS_PRESENT = ClassUtils.isPresent("com.ciicgat.sdk.gconf.ConfigCollection");

    private static final boolean SPRING_MVC_PRESENT = ClassUtils.isPresent("org.springframework.web.bind.annotation.RequestMapping");

    static {
        LOGGER.info("trace status {}", TRACE_CLASS_PRESENT);
        LOGGER.info("gconf status {}", GCONF_CLASS_PRESENT);
        LOGGER.info("spring mvc status={}", SPRING_MVC_PRESENT);
    }

    public static boolean isTraceEnable() {
        return TRACE_CLASS_PRESENT;
    }

    public static boolean isGconfEnable() {
        return GCONF_CLASS_PRESENT;
    }

    public static boolean isSpringMvcEnable() {
        return SPRING_MVC_PRESENT;
    }

}
