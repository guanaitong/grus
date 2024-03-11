/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.exception;


import com.ciicgat.sdk.lang.convert.ErrorCode;

/**
 * Created by August.Zhou on 2017/7/27 17:46.
 */
public class BusinessException extends Exception {

    private static final long serialVersionUID = 1L;

    private int errorCode;

    private String errorMsg;

    public BusinessException(int errorCode, String errorMsg) {
        super("errorCode=" + errorCode + ", errorMsg=" + errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusinessException(ErrorCode errorCode) {
        this(errorCode.getErrorCode(), errorCode.getErrorMsg());
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String toString() {
        return "BusinessException occurred, errorCode=" + errorCode + ",errorMsg=" + errorMsg;
    }

}
