/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel.annotation;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by August.Zhou on 2022/10/20 17:44
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleFlowRule {
    /**
     * 限流阈值类型，线程数模式或QPS  (0: thread count, 1: QPS).
     *
     * @return
     */
    int grade() default RuleConstant.FLOW_GRADE_THREAD;

    /**
     * Flow control threshold count.
     * 如果grade为qps，那么为resource能成熟的最大qps
     * 如果grade为thread，那么为resource能成熟的最大并发
     *
     * @return
     */
    int value();

    int controlBehavior() default RuleConstant.CONTROL_BEHAVIOR_DEFAULT;
}

