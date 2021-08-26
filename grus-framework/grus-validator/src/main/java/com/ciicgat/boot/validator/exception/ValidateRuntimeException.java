/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.exception;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.sdk.lang.convert.ErrorCode;

/**
 * Created by Josh on 17-11-9.
 */
public class ValidateRuntimeException extends RuntimeException implements ErrorCode {

    private int code;
    private FailedReason failedReason;

    public ValidateRuntimeException() {

    }

    public ValidateRuntimeException(int code, FailedReason failedReason) {
        this.code = code;
        this.failedReason = failedReason;
    }

    public int getCode() {
        return code;
    }

    public FailedReason getFailedReason() {
        return failedReason;
    }

    @Override
    public String getMessage() {
        return failedReason == null ? "" : failedReason.toString();
    }

    @Override
    public int getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return this.getMessage();
    }
}
