/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import com.ciicgat.sdk.lang.constant.GatBoolean;
import com.ciicgat.sdk.lang.convert.BaseErrorCode;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import com.ciicgat.sdk.lang.convert.StandardErrorCode;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by August.Zhou on 2018-10-22 14:31.
 */
public class TestClassUtils {


    @Test
    public void test() {
        List<Class<?>> classes = ClassUtils.getClasses("com.ciicgat.sdk.lang.constant");
        Assert.assertTrue(classes.contains(GatBoolean.class));
    }

    @Test
    public void test1() {
        List<Class> classes = ClassUtils.getAllClassByInterface(ErrorCode.class);
        Assert.assertTrue(classes.contains(BaseErrorCode.class));
        Assert.assertTrue(classes.contains(StandardErrorCode.class));
    }
}
