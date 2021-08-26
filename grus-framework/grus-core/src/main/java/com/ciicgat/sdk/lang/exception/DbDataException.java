/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.exception;

/**
 * 数据库异常
 * Created by August.Zhou on 2018-10-22 13:32.
 */
public class DbDataException extends RuntimeException {

    public DbDataException() {
        super();
    }

    public DbDataException(String message) {
        super(message);
    }

    public DbDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public DbDataException(Throwable cause) {
        super(cause);
    }
}
