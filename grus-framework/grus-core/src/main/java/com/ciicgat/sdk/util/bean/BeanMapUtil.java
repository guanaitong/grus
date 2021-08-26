/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/15 15:10
 * @Description:
 */
public class BeanMapUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanMapUtil.class);

    @SuppressWarnings("uncheck")
    public static Map<String, Object> bean2Map(Object bean) {
        if (bean == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();

        BeanMap beanMap = BeanMap.create(bean);
        map.putAll(beanMap);

        return map;
    }

    public static <T> T map2Bean(Map<String, Object> map, Class<T> beanClass) {
        T bean;
        try {
            bean = beanClass.getDeclaredConstructor().newInstance();
            BeanMap beanMap = BeanMap.create(bean);
            beanMap.putAll(map);
        } catch (Exception e) {
            LOGGER.error("BeanMapUtil cglib copy error", e);
            throw new RuntimeException(e);
        }
        return bean;
    }


}
