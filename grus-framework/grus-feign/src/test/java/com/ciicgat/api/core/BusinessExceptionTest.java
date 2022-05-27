/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.sdk.lang.convert.BaseErrorCode;
import com.ciicgat.sdk.lang.exception.BusinessException;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

/**
 * @author Clive Yuan
 * @date 2022/05/27
 */
public class BusinessExceptionTest {
    private static final int CODE = 1024;
    private static final String MSG = "code error";
    private static final String MESSAGE = "errorCode=" + CODE + ", errorMsg=" + MSG;
    private static final BaseErrorCode BASE_ERROR_CODE = new BaseErrorCode(CODE, MSG);

    @Test
    public void testBusinessException() {
        try {
            throw new BusinessException(BASE_ERROR_CODE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.assertNotNull(e.toString());
            Assert.assertEquals(MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testBusinessException2() {
        try {
            throw new BusinessException(BASE_ERROR_CODE);
        } catch (BusinessException e) {
            Assertions.assertEquals(CODE, e.getErrorCode());
            Assertions.assertEquals(MSG, e.getErrorMsg());
        }
    }

    @Test
    public void testBusinessRuntimeException() {
        try {
            throw new BusinessRuntimeException(BASE_ERROR_CODE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.assertEquals(MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testBusinessFeignException() {
        try {
            throw new BusinessFeignException(20, CODE, MSG);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.assertEquals(MESSAGE, e.getMessage());
        }
    }
}
