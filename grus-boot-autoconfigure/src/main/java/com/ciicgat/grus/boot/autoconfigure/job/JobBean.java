/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.job;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by August.Zhou on 2019-04-04 10:22.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface JobBean {

    /**
     * 任务的名字
     *
     * @return
     */
    String jobName();


    /**
     * cron表达式,类似于 0 0/550 * * * ?
     *
     * @return
     */
    String cron();

    /**
     * 描述
     *
     * @return
     */
    String description() default "";

    int shardingTotalCount() default 1;

    String shardingItemParameters() default "";


}
