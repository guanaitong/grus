/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.exception;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/26 13:35
 * @Description:
 */
public class ZKException extends Exception {

    public ZKException() {
        super();
    }

    public ZKException(String message) {
        super(message);
    }

    public ZKException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZKException(Throwable cause) {
        super(cause);
    }
}
