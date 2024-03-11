/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

import java.util.Collections;
import java.util.Map;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/15 15:10
 * @Description:
 */
public class BeanMapUtil {

    public static Map<String, Object> bean2Map(Object bean) {
        if (bean == null) {
            return Collections.emptyMap();
        }
        return BeanCopyUtil.INSTANCE.bean2Map(bean);
    }

    public static <T> T map2Bean(Map<String, Object> map, Class<T> beanClass) {
        return BeanCopyUtil.INSTANCE.map2Bean(map, beanClass);
    }

}
