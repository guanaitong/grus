/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * Created by August.Zhou on 2019-07-03 16:14.
 */
public class TypeTest {

    @Test
    public void test() throws Exception {
        Assertions.assertTrue(ObjectValidator.isIterableType(ArrayList.class));
        Assertions.assertFalse(ObjectValidator.isIterableType(String.class));
        Assertions.assertTrue(ObjectValidator.isArrayType(String[].class));

        Class<?> componentType = String[].class.getComponentType();
        System.out.println(componentType);

    }
}
