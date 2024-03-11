/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.exception;


import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.convert.ErrorCode;

/**
 * Created by August.Zhou on 2017/1/6 10:03.
 */
public class BusinessRuntimeException extends RuntimeException implements ErrorCode {

    private static final long serialVersionUID = 1L;

    // 默认值是200
    private final int httpStatusCode;

    private final int errorCode;

    private final String errorMsg;

    private final Object data;

    public BusinessRuntimeException(ErrorCode errorCode) {
        this(errorCode.getErrorCode(), errorCode.getErrorMsg());
    }

    public BusinessRuntimeException(int errorCode, String errorMsg) {
        this(errorCode, errorMsg, null, 200);
    }

    public BusinessRuntimeException(ErrorCode errorCode, Object data) {
        this(errorCode, data, 200);
    }

    public BusinessRuntimeException(ErrorCode errorCode, Object data, int httpStatusCode) {
        this(errorCode.getErrorCode(), errorCode.getErrorMsg(), data, httpStatusCode);
    }

    public BusinessRuntimeException(int errorCode, String errorMsg, Object data, int httpStatusCode) {
        super("errorCode=" + errorCode + ", errorMsg=" + errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.data = data;
        this.httpStatusCode = httpStatusCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Object getData() {
        return data;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public <T> ApiResponse<T> fail() {
        return ApiResponse.fail(this, (T) data);
    }

    public String toString() {
        return "BusinessRuntimeException occurred, errorCode=" + errorCode + ",errorMsg=" + errorMsg;
    }

}
