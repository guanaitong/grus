/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.exception;


import com.ciicgat.sdk.lang.convert.ErrorCode;

/**
 * Created by August.Zhou on 2017/1/6 10:03.
 */
public class BusinessRuntimeException extends RuntimeException implements ErrorCode {

    private static final long serialVersionUID = 1L;

    private int errorCode;

    private String errorMsg;

    public BusinessRuntimeException(int errorCode, String errorMsg) {
        super(errorCode + "");
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusinessRuntimeException(ErrorCode errorCode) {
        this(errorCode.getErrorCode(), errorCode.getErrorMsg());
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BusinessRuntimeException occurred, errorCode=" + errorCode + ",errorMsg=" + errorMsg);
        return sb.toString();
    }

}
