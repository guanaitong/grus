/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标示使用本地缓存
 * Created by Jiaju.wei on 2018/6/15 14:22.
 *
 * @memo 对得到的缓存对象做赋值操作，会同时改变缓存值，使用者需要审慎评估
 */
@Retention(RUNTIME)
@Target({METHOD})
public @interface ApiCache {

    /**
     * 参数的序列号，按顺序组装缓存key，不传将以方法名做key
     * 确保参数非空时toString不会给出"null"结果
     */
    int[] params() default {};

    /**
     * 缓存过期时间
     */
    long expireSeconds() default 300;

    /**
     * 最大缓存大小
     */
    long maxCacheSize() default 10240L;

    /**
     * 并发级别，不再生效
     */
    @Deprecated
    int concurrencyLevel() default 16;


    /**
     * 当值为null时，是否缓存，默认为true
     *
     * @return
     */
    boolean cacheNullValue() default true;

}
