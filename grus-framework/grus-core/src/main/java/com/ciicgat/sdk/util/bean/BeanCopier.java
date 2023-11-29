/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

import java.util.Map;

/**
 * Created by August.Zhou on 2023/11/15 21:14
 */
public interface BeanCopier {
    void copyProperties(Object source, Object target);

    <T> T copy(Object src, Class<T> dstClass);

    Map<String, Object> bean2Map(Object bean);

    <T> T map2Bean(Map<String, Object> map, Class<T> beanClass);

}
