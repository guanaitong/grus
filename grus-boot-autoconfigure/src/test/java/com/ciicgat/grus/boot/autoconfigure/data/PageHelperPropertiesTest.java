/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by August.Zhou on 2021/9/2 15:58.
 */
class PageHelperPropertiesTest {

    @Test
    public void test() {
        PageHelperProperties pageHelperProperties = new PageHelperProperties();
        pageHelperProperties.setHelperDialect("xx");
        assertEquals("xx", pageHelperProperties.getHelperDialect());

        pageHelperProperties.setAutoDialect("true");
        assertEquals("true", pageHelperProperties.getAutoDialect());

        pageHelperProperties.setDialectAlias("xxx");
        pageHelperProperties.setDefaultCount("20");
        pageHelperProperties.setAutoRuntimeDialect(true);
        pageHelperProperties.setParams("xxx");
        pageHelperProperties.setSupportMethodsArguments(true);
        pageHelperProperties.setReasonable(true);
        pageHelperProperties.setCloseConn(true);
        pageHelperProperties.setPageSizeZero(true);
    }
}
