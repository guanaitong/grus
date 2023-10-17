/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.springservice;

import java.time.Duration;

/**
 * Created by August.Zhou on 2023/10/30 16:59
 */
public record ServiceCacheKey(Class<?> serviceClazz, Duration blockTimeout) {
}
