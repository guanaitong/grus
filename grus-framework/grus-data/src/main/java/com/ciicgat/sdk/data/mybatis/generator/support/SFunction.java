/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.support;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 支持序列化的 Function
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
