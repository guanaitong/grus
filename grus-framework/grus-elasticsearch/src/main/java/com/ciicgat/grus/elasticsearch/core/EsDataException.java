/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

/**
 * Created by August.Zhou on 2019-09-09 10:51.
 */
public class EsDataException extends RuntimeException {

    public EsDataException() {
    }

    public EsDataException(String message) {
        super(message);
    }

    public EsDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public EsDataException(Throwable cause) {
        super(cause);
    }
}
