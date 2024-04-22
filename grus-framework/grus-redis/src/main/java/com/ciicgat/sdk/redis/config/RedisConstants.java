/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis.config;

/**
 * @author August
 * @date 2020/3/6 6:04 PM
 */
public interface RedisConstants {

    int DEFAULT_MAX_IDLE = 128;
    int DEFAULT_MIN_IDLE = 1;
    int DEFAULT_MAX_ACTIVE = 128;
    int DEFAULT_MAX_WAIT_MILLIS = 50;

    int DEFAULT_CONNECT_TIME_OUT_MILLIS = 500;
    int DEFAULT_TIME_OUT_MILLIS = 1000;

    int DEFAULT_MAX_ATTEMPTS = 3;

}
