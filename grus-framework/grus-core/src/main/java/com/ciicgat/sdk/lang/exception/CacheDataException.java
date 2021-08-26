/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.exception;

/**
 * 缓存异常
 * <p>
 * Created by August.Zhou on 2016/12/30 17:22.
 */
public class CacheDataException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CacheDataException() {
        super();
    }

    public CacheDataException(String message) {
        super(message);
    }

    public CacheDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheDataException(Throwable cause) {
        super(cause);
    }
}
