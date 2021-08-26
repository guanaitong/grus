/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

/**
 * Created by August.Zhou on 2019-09-11 13:17.
 */
public interface EntityMapper<T extends IndexAble> {
    String mapToString(Object object);

    T mapToObject(String source, Class<T> clazz);

}
