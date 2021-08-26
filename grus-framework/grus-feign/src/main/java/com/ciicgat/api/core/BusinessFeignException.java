/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.sdk.lang.convert.ErrorCenter;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import feign.FeignException;

/**
 * the exception must extend FeignException,otherwise it will be wrapped
 * <p>
 * Created by August.Zhou on 2017/7/31 18:45.
 */
public class BusinessFeignException extends FeignException implements ErrorCode {

    private static final long serialVersionUID = 1L;

    private final int errorCode;

    private final String errorMsg;


    private ErrorCode error;


    public BusinessFeignException(int status, int errorCode, String errorMsg) {
        super(status, "", new byte[0]);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public final int getErrorCode() {
        return errorCode;
    }

    public final String getErrorMsg() {
        return errorMsg;
    }


    @SuppressWarnings("unchecked")
    public <T extends ErrorCode> T getError() {
        if (error == null) {
            error = ErrorCenter.valueOf(errorCode);
        }
        return (T) error;
    }

    @Override
    public String toString() {
        return "BusinessFeignException{" +
                "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", error=" + error +
                '}';
    }
}
