/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.feign;

/**
 * Created by August.Zhou on 2019-03-05 13:33.
 */
public @interface CacheBinding {

    String method() default "";

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
     * 并发级别
     */
    int concurrencyLevel() default 16;


    /**
     * 当值为null时，是否缓存，默认为true
     *
     * @return
     */
    boolean cacheNullValue() default true;
}
