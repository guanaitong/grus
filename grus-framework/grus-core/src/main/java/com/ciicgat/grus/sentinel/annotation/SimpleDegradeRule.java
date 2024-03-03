/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 降级规则定义，
 * resourceName 统一使用serviceName+classname+methodName
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface SimpleDegradeRule {

    /**
     * 熔断策略(0: average RT, 1: exception ratio, 2: exception count).默认0
     *
     * @return
     */
    int grade() default 0;

    /**
     * 慢调用比例模式下为慢调用临界 RT（超出该值计为慢调用）；异常比例/异常数模式下为对应的阈值
     *
     * @return
     */
    double value();

    /**
     * 熔断时长，单位为 s, 默认30s
     *
     * @return
     */
    int timeWindow() default 30;

    /**
     * 统计时长（单位为 ms），默认1秒
     *
     * @return
     */
    int statIntervalMs() default 1000;

    /**
     * 熔断触发的最小请求数, 默认5
     *
     * @return
     */
    int minRequestAmount() default 5;

    /**
     * 慢调用比例阈值，仅慢调用比例模式有效
     *
     * @return
     */
    double slowRatioThreshold() default 0.5d;

}
