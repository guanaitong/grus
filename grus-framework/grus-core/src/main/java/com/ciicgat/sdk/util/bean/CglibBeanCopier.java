/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by August.Zhou on 2023/11/15 21:04
 */
class CglibBeanCopier implements com.ciicgat.sdk.util.bean.BeanCopier {
    static com.ciicgat.sdk.util.bean.BeanCopier INSTANCE = new CglibBeanCopier();
    private static final Logger LOGGER = LoggerFactory.getLogger(CglibBeanCopier.class);

    private ConcurrentHashMap<String, BeanCopier> cache = new ConcurrentHashMap<>();

    private CglibBeanCopier() {
    }

    public <T> T copy(Object src, Class<T> dstClass) {
        BeanCopier beanCopier = getCopier(src.getClass(), dstClass);
        T dst;
        try {
            dst = dstClass.getDeclaredConstructor().newInstance();
            beanCopier.copy(src, dst, null);
        } catch (Exception e) {
            LOGGER.error("BeanCopyUtil cglib copy error", e);
            throw new RuntimeException(e);
        }
        return dst;
    }

    private BeanCopier getCopier(Class<?> srcClass, Class<?> dstClass) {
        String key = srcClass.getName() + dstClass.getName();
        BeanCopier beanCopier = cache.get(key);
        if (null == beanCopier) {
            BeanCopier newBeanCopier = BeanCopier.create(srcClass, dstClass, false);
            beanCopier = cache.putIfAbsent(key, newBeanCopier);
            // 线程安全，保证取到的是一个copier
            if (beanCopier == null) {
                beanCopier = newBeanCopier;
            }
        }

        return beanCopier;
    }

    @Override
    public Map<String, Object> bean2Map(Object bean) {
        Map<String, Object> map = new HashMap<>();
        BeanMap beanMap = BeanMap.create(bean);
        map.putAll(beanMap);
        return map;
    }

    @Override
    public <T> T map2Bean(Map<String, Object> map, Class<T> beanClass) {
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
