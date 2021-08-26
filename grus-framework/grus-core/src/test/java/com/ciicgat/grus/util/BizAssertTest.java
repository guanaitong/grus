/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.util;

import com.ciicgat.sdk.lang.convert.BaseErrorCode;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Stanley Shen stanley.shen@guanaitong.com
 * @version 2020-05-17 15:36
 */
public class BizAssertTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizAssertTest.class);

    @Test
    public void notEmpty() {
        BizAssert.notEmpty(Arrays.asList(1, 2), new BaseErrorCode(-1, "empty"), () -> LOGGER.error("empty"));
        BizAssert.notEmpty(Arrays.asList(1, 2), new BaseErrorCode(-1, "empty"));
        try {
            BizAssert.notEmpty(new ArrayList<>(), new BaseErrorCode(-1, "empty"), () -> {
                LOGGER.error("empty");
            });
        } catch (BusinessRuntimeException exception) {
            Assert.assertEquals(-1, exception.getErrorCode());
            Assert.assertEquals("empty", exception.getErrorMsg());
        }
    }

    @Test
    public void notBlank() {
        AtomicInteger index = new AtomicInteger(0);
        BizAssert.notBlank("1", new BaseErrorCode(-1, "empty"), () -> LOGGER.error("empty"));
        BizAssert.notBlank("1", new BaseErrorCode(-1, "empty"));
        try {
            BizAssert.notBlank("", new BaseErrorCode(-1, "empty"), () -> index.addAndGet(1));
        } catch (BusinessRuntimeException exception) {
            Assert.assertEquals(-1, exception.getErrorCode());
            Assert.assertEquals("empty", exception.getErrorMsg());
            Assert.assertEquals(1, index.get());
        }
    }

}
