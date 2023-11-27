/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;

/**
 * Created by August.Zhou on 2023/11/15 21:21
 */
class JdkBeanCopier implements BeanCopier {
    static BeanCopier INSTANCE = new JdkBeanCopier();
    private JdkBeanCopier() {
    }
    @Override
    public <T> T copy(Object src, Class<T> dstClass) {
        T dst = BeanUtils.instantiateClass(dstClass);
        BeanUtils.copyProperties(src, dst);
        return dst;
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
                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                    readMethod.setAccessible(true);
                }
                try {
                    Object value = readMethod.invoke(bean);
                    if (Objects.nonNull(value)) {
                        map.put(name, value);
                    }
                } catch (Exception ex) {
                    throw new FatalBeanException(
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
                try {
                    Object value = map.get(name);
                    if (Objects.nonNull(value)) {
                        if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                            writeMethod.setAccessible(true);
                        }
                        writeMethod.invoke(bean, value);
                    }
                } catch (Exception ex) {
                    throw new FatalBeanException(
                            "Could not copy property '" + name + "' from source to target", ex);
                }
            }
        }
        return bean;
    }
}
