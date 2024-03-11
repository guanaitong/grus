/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.util;

/**
 * Created by August.Zhou on 2022/10/25 10:42
 */
public class ClassType {
    private String className;
    private boolean exist;
    private Class expectClazz;

    public ClassType(String className) {
        this.className = className;
        try {
            expectClazz = Class.forName(className);
            this.exist = true;
        } catch (ClassNotFoundException e) {
            this.exist = false;
        }
    }

    public boolean isInstance(Object object) {
        return isClass(object.getClass());
    }

    public boolean isClass(Class<?> aClass) {
        if (exist) {
            return expectClazz.isAssignableFrom(aClass);
        }
        return false;
    }
}
