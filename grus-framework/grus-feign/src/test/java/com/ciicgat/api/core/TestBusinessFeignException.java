/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.sdk.lang.convert.BaseErrorCode;
import feign.FeignException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by August.Zhou on 2018-10-22 14:49.
 */
public class TestBusinessFeignException {

    @Test(expected = BusinessFeignException.class)
    public void testBusinessRuntimeException() {
        BaseErrorCode baseErrorCode = new BaseErrorCode(22, "asdf");
        BusinessFeignException businessFeignException = new BusinessFeignException(400, 22, "asdf");
        Assert.assertNotNull(businessFeignException.toString());
        Assert.assertTrue((businessFeignException instanceof FeignException));

        Assert.assertTrue(businessFeignException.getErrorCode() == baseErrorCode.getErrorCode());
        Assert.assertTrue(businessFeignException.getErrorMsg() == baseErrorCode.getErrorMsg());
        Assert.assertSame(baseErrorCode, businessFeignException.getError());
        throw businessFeignException;
    }

}
