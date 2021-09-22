/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
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



    static {
        LOGGER.info("trace status {}", TRACE_CLASS_PRESENT);
    }

    public static boolean isTraceEnable() {
        return TRACE_CLASS_PRESENT;
    }

}
