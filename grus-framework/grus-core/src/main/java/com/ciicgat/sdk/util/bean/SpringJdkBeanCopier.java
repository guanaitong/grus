/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;

/**
 * Created by August.Zhou on 2023/11/15 21:21
 */
class SpringJdkBeanCopier implements BeanCopier {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringJdkBeanCopier.class);
    static BeanCopier INSTANCE = new SpringJdkBeanCopier();

    private SpringJdkBeanCopier() {
    }

    @Override
    public void copyProperties(Object source, Object target) {
        try {
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            LOGGER.error("BeanCopyUtil spring jdk copy error", e);
            throw new BeansException("spring jdk copy error", e);
        }
    }

    @Override
    public <T> T copy(Object src, Class<T> dstClass) {
        try {
            T dst = BeanUtils.instantiateClass(dstClass);
            BeanUtils.copyProperties(src, dst);
            return dst;
        } catch (Exception e) {
            LOGGER.error("BeanCopyUtil spring jdk copy error", e);
            throw new BeansException("spring jdk copy error", e);
        }
    }

    @Override
    public Map<String, Object> bean2Map(Object bean) {
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(bean.getClass());
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(propertyDescriptors.length);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            if ("class".equals(name)) {
                continue;
            }
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                int modifiers = readMethod.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    continue;
                }
                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                    readMethod.setAccessible(true);
                }
                try {
                    Object value = readMethod.invoke(bean);
                    if (Objects.nonNull(value)) {
                        map.put(name, value);
                    }
                } catch (Exception ex) {
                    throw new BeansException(
                            "Could not copy property '" + name + "' from source to target", ex);
                }
            }
        }
        return map;
    }

    @Override
    public <T> T map2Bean(Map<String, Object> map, Class<T> beanClass) {
        T bean = BeanUtils.instantiateClass(beanClass);
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(beanClass);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            if ("class".equals(name)) {
                continue;
            }
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod != null) {
                int modifiers = writeMethod.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    continue;
                }
                try {
                    Object value = map.get(name);
                    if (Objects.nonNull(value)) {
                        if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                            writeMethod.setAccessible(true);
                        }
                        writeMethod.invoke(bean, value);
                    }
                } catch (Exception ex) {
                    throw new BeansException(
                            "Could not copy property '" + name + "' from source to target", ex);
                }
            }
        }
        return bean;
    }
}
