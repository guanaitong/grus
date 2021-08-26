/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.exception;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/2 11:01
 * @Description:
 */
public class ZKRuntimeException extends RuntimeException {
    public ZKRuntimeException() {
        super();
    }

    public ZKRuntimeException(String message) {
        super(message);
    }

    public ZKRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZKRuntimeException(Throwable cause) {
        super(cause);
    }
}
