/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.bean;

import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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

    public static <T> T map2Bean(Map<String, Object> map, Class<T> beanClass) {
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
