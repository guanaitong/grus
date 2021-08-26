/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by Josh on 17-11-9.
 */
public class ValidateTest2 {


    @Test
    public void testSuccess() throws Exception {
        TestMethod testMethod = new TestMethod();
        testMethod.setValue("12", 2L, null);

        TestBean testBean = new TestBean();
        testBean.setTestString("123123");
        testBean.setSortMode("ID_ASC");
        testBean.setTestLong(3L);

        RootObjectValidator rootObjectValidator = new RootObjectValidator();


        ValidateResult validateResult = rootObjectValidator.validate(testBean);

        Assert.assertTrue(validateResult.isValid());

    }

    @Test
    public void testSuccess1() throws Exception {


        TestBean2 testBean2 = new TestBean2();
        testBean2.setTestString("123123");
        testBean2.setSortMode("ID_ASC");
        testBean2.setTestLong(3L);

        TestBean3 testBean3 = new TestBean3();
        testBean3.setTestString("123123");
        testBean3.setSortMode("ID_ASC");
        testBean3.setTestLong(3L);

        testBean2.setTestBean3List(Arrays.asList(testBean3));
        testBean2.setTestBean3s(new TestBean3[]{testBean3});

        RootObjectValidator rootObjectValidator = new RootObjectValidator();


        ValidateResult validateResult = rootObjectValidator.validate(testBean2);

        Assert.assertTrue(validateResult.isValid());

    }

    @Test
    public void testFail() throws Exception {
        TestMethod testMethod = new TestMethod();
        testMethod.setValue("12", 2L, null);

        TestBean testBean = new TestBean();
        testBean.setTestString("123123");
        testBean.setSortMode("ID_ASC");

        RootObjectValidator rootObjectValidator = new RootObjectValidator();


        ValidateResult validateResult = rootObjectValidator.validate(testBean);

        Assert.assertTrue(!validateResult.isValid());
    }

    @Test
    public void testFail2() throws Exception {
        TestMethod testMethod = new TestMethod();
        testMethod.setValue("12", 2L, null);

        TestBean testBean = new TestBean();
        testBean.setTestString("123123");
        testBean.setSortMode("1");

        RootObjectValidator rootObjectValidator = new RootObjectValidator();


        ValidateResult validateResult = rootObjectValidator.validate(testBean);

        Assert.assertTrue(!validateResult.isValid());
    }

}
