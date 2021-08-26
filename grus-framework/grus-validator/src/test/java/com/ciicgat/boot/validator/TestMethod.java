/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import com.ciicgat.boot.validator.annotation.Min;
import com.ciicgat.boot.validator.annotation.NotNull;

/**
 * Created by August.Zhou on 2019-04-17 10:14.
 */
public class TestMethod {

    public void setValue(@NotNull String testString, @NotNull @Min(1) Long testLong, TestBean testBean) {
        return;
    }
}
