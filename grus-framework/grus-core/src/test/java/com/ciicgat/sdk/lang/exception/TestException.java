/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.exception;

import com.ciicgat.sdk.lang.convert.BaseErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2018-10-22 11:05.
 */
public class TestException {


    @Test
    public void testCacheException() {
        CacheDataException cacheException = new CacheDataException();
        Assertions.assertTrue(cacheException instanceof RuntimeException);

        CacheDataException cacheException1 = new CacheDataException("xx", new Exception("asdf"));
        CacheDataException cacheException2 = new CacheDataException("xx");
        CacheDataException cacheException3 = new CacheDataException(new Exception("asdf"));
    }

    @Test
    public void testDataLoaderException() {
        DbDataException dataLoaderException = new DbDataException();
        Assertions.assertTrue(dataLoaderException instanceof RuntimeException);

        DbDataException dataLoaderException1 = new DbDataException("xx", new Exception("asdf"));
        DbDataException dataLoaderException2 = new DbDataException("xx");
        DbDataException dataLoaderException3 = new DbDataException(new Exception("asdf"));
    }

    @Test
    public void testBusinessRuntimeException() {
        BusinessRuntimeException businessRuntimeException = new BusinessRuntimeException(22, "asdf");
        Assertions.assertNotNull(businessRuntimeException.toString());
        Assertions.assertTrue(businessRuntimeException instanceof RuntimeException);

        BusinessRuntimeException businessRuntimeException1 = new BusinessRuntimeException(new BaseErrorCode(22, "asdf"));
        Assertions.assertTrue(businessRuntimeException.getErrorCode() == businessRuntimeException1.getErrorCode());
        Assertions.assertTrue(businessRuntimeException.getErrorMsg() == businessRuntimeException1.getErrorMsg());
    }

    @Test
    public void testBusinessException() throws BusinessException {
        BusinessException businessException = new BusinessException(22, "asdf");
        Assertions.assertNotNull(businessException.toString());
        Assertions.assertTrue(businessException instanceof Exception);

        BusinessException businessException1 = new BusinessException(new BaseErrorCode(22, "asdf"));
        Assertions.assertTrue(businessException.getErrorCode() == businessException1.getErrorCode());
        Assertions.assertTrue(businessException.getErrorMsg() == businessException1.getErrorMsg());
    }
}
