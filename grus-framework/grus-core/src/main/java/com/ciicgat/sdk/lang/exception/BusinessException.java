/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
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
        StringBuilder sb = new StringBuilder();
        sb.append("BussinessException occurred, errorCode=" + errorCode + ",errorMsg=" + errorMsg);
        return sb.toString();
    }

}
