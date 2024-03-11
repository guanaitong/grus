/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.sdk.lang.convert.BaseErrorCode;
import com.ciicgat.sdk.lang.convert.ErrorCenter;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2018-10-22 14:49.
 */
public class TestBusinessFeignException {

    @Test
    public void testBusinessRuntimeException() {
        BaseErrorCode baseErrorCode = new BaseErrorCode(22, "asdf");
        BusinessFeignException businessFeignException = new BusinessFeignException(400, 22, "asdf");
        ErrorCenter.put(baseErrorCode);
        Assertions.assertNotNull(businessFeignException.toString());
        Assertions.assertTrue((businessFeignException instanceof FeignException));

        Assertions.assertTrue(businessFeignException.getErrorCode() == baseErrorCode.getErrorCode());
        Assertions.assertTrue(businessFeignException.getErrorMsg() == baseErrorCode.getErrorMsg());
        Assertions.assertSame(baseErrorCode, businessFeignException.getError());
    }

}
