/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by August.Zhou on 2019-07-03 16:14.
 */
public class TypeTest {

    @Test
    public void test() throws Exception {
        Assert.assertTrue(ObjectValidator.isIterableType(ArrayList.class));
        Assert.assertFalse(ObjectValidator.isIterableType(String.class));
        Assert.assertTrue(ObjectValidator.isArrayType(String[].class));

        Class<?> componentType = String[].class.getComponentType();
        System.out.println(componentType);

    }
}
