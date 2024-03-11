/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

/**
 * Created by August.Zhou on 2023/11/30 12:42
 */
@FunctionalInterface
public interface SlowLoggerFormatter<T> {

    String format(T value);
}
