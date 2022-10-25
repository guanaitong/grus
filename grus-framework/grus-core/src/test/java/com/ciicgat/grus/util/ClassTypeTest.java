/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by August.Zhou on 2022/10/25 10:52
 */
class ClassTypeTest {

    @Test
    void isInstance() {
        String name = new A().getClass().getName();
        System.out.println(name);
        ClassType classType = new ClassType(new A().getClass().getName());

        assertFalse(classType.isInstance(new Object()));
        assertFalse(classType.isInstance(new ClassTypeTest()));
        assertTrue(classType.isInstance(new A()));
        assertTrue(classType.isInstance(new B()));

        ClassType classTypeXXX = new ClassType("xxx.yyy.zzz");
        assertFalse(classTypeXXX.isInstance(new Object()));
    }

    private static class A {

    }

    private static class B extends A {

    }
}
