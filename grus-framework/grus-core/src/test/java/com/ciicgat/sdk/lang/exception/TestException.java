/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.exception;

import com.ciicgat.sdk.lang.convert.BaseErrorCode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by August.Zhou on 2018-10-22 11:05.
 */
public class TestException {


    @Test(expected = CacheDataException.class)
    public void testCacheException() {
        CacheDataException cacheException = new CacheDataException();
        Assert.assertTrue(cacheException instanceof RuntimeException);

        CacheDataException cacheException1 = new CacheDataException("xx", new Exception("asdf"));
        CacheDataException cacheException2 = new CacheDataException("xx");
        CacheDataException cacheException3 = new CacheDataException(new Exception("asdf"));
        throw cacheException;
    }

    @Test(expected = DbDataException.class)
    public void testDataLoaderException() {
        DbDataException dataLoaderException = new DbDataException();
        Assert.assertTrue(dataLoaderException instanceof RuntimeException);

        DbDataException dataLoaderException1 = new DbDataException("xx", new Exception("asdf"));
        DbDataException dataLoaderException2 = new DbDataException("xx");
        DbDataException dataLoaderException3 = new DbDataException(new Exception("asdf"));
        throw dataLoaderException;
    }

    @Test(expected = BusinessRuntimeException.class)
    public void testBusinessRuntimeException() {
        BusinessRuntimeException businessRuntimeException = new BusinessRuntimeException(22, "asdf");
        Assert.assertNotNull(businessRuntimeException.toString());
        Assert.assertTrue(businessRuntimeException instanceof RuntimeException);

        BusinessRuntimeException businessRuntimeException1 = new BusinessRuntimeException(new BaseErrorCode(22, "asdf"));
        Assert.assertTrue(businessRuntimeException.getErrorCode() == businessRuntimeException1.getErrorCode());
        Assert.assertTrue(businessRuntimeException.getErrorMsg() == businessRuntimeException1.getErrorMsg());
        throw businessRuntimeException;
    }

    @Test(expected = BusinessException.class)
    public void testBusinessException() throws BusinessException {
        BusinessException businessException = new BusinessException(22, "asdf");
        Assert.assertNotNull(businessException.toString());
        Assert.assertTrue(businessException instanceof Exception);

        BusinessException businessException1 = new BusinessException(new BaseErrorCode(22, "asdf"));
        Assert.assertTrue(businessException.getErrorCode() == businessException1.getErrorCode());
        Assert.assertTrue(businessException.getErrorMsg() == businessException1.getErrorMsg());
        throw businessException;
    }
}
