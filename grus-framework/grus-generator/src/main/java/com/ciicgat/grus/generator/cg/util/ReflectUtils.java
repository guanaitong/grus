/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.util;

/**
 * 反射工具类
 *
 * @author Clive Yuan
 * @date 2020/11/05
 */
public class ReflectUtils {

    /**
     * 初始化
     *
     * @param clazz 类
     * @param <T>   泛型
     * @return
     */
    public static <T> T newInstance(Class<?> clazz) {
        try {
            return (T) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
