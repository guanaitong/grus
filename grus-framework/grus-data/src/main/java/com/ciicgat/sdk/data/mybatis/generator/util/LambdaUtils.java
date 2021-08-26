/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.mybatis.generator.util;

import com.ciicgat.sdk.data.mybatis.generator.support.SFunction;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lambda工具
 *
 * @author Clive Yuan
 * @date 2020/10/29
 */
public class LambdaUtils {

    /**
     * SerializedLambda 反序列化缓存
     */
    private static final Map<String, SerializedLambda> FUNC_CACHE = new ConcurrentHashMap<>();

    public static <T> String getFieldName(SFunction<T, ?> func) {
        SerializedLambda serializedLambda = resolveSerializedLambdaCache(func);
        String getter = serializedLambda.getImplMethodName();
        return resolveFieldName(getter);
    }

    private static <T> SerializedLambda resolveSerializedLambdaCache(SFunction<T, ?> func) {
        Class<?> clazz = func.getClass();
        String name = clazz.getName();
        return Optional.ofNullable(FUNC_CACHE.get(name))
                .orElseGet(() -> {
                    SerializedLambda lambda = resolveSerializedLambda(func);
                    FUNC_CACHE.put(name, lambda);
                    return lambda;
                });
    }

    private static <T> SerializedLambda resolveSerializedLambda(SFunction<T, ?> func) {
        try {
            // 通过获取对象方法，判断是否存在该方法
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            // 利用jdk的SerializedLambda 解析方法引用
            return (SerializedLambda) method.invoke(func);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static String resolveFieldName(String getMethodName) {
        if (getMethodName.startsWith("get")) {
            getMethodName = getMethodName.substring(3);
        } else if (getMethodName.startsWith("is")) {
            getMethodName = getMethodName.substring(2);
        }
        // 小写第一个字母
        return SqlUtils.firstToLowerCase(getMethodName);
    }


}
