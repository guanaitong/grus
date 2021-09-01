/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by City Mo on 2017/12/11.
 */
public class SupportEnumType {
    private static final Logger LOGGER = LoggerFactory.getLogger(SupportEnumType.class);
    static final Set<Class> set = new HashSet<>();

    static {
        set.add(Integer.TYPE);
        set.add(Integer.class);
        set.add(int.class);

        set.add(Long.TYPE);
        set.add(Long.class);
        set.add(long.class);

        set.add(Double.TYPE);
        set.add(Double.class);
        set.add(double.class);

        set.add(Float.TYPE);
        set.add(Float.class);
        set.add(float.class);

        set.add(String.class);
    }

    public static boolean support(Class clazz) {
        return set.contains(clazz);
    }

    public static Method getMethod(Class clazz, String methodName) {
        try {
            Method method = clazz.getDeclaredMethod(methodName);
            return method;
        } catch (Exception e) {
            try {
                Method method = clazz.getMethod(methodName);
                return method;
            } catch (Exception ex) {
            }
            LOGGER.error("枚举类{}中，方法{}不存在", clazz.getCanonicalName(), methodName);
            LOGGER.error("枚举值获取异常", e);
        }
        return null;
    }
}
