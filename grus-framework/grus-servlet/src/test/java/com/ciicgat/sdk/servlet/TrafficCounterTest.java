/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Map;

import static com.ciicgat.sdk.servlet.TrafficCounter.trafficIncrement;

class TrafficCounterTest {
    @Test
    void test() {
        trafficIncrement();
        trafficIncrement();
        trafficIncrement();
        Map<String, Long> counterMap = TrafficCounter.getCounter();
        Assertions.assertTrue(counterMap.size() > 0);
        final Constructor<?>[] constructors = TrafficCounter.class.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Assertions.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }
    }
}
