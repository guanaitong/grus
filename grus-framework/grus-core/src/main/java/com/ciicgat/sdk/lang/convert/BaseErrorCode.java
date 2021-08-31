/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.convert;


import com.ciicgat.grus.json.JSON;

/**
 * Created by August.Zhou on 2017/4/27 19:03.
 */
public class BaseErrorCode implements ErrorCode {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final int errorCode;

    private final String errorMsg;

    public BaseErrorCode(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        ErrorCenter.put(this);
    }


    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseErrorCode)) return false;

        BaseErrorCode that = (BaseErrorCode) o;

        if (getErrorCode() != that.getErrorCode()) return false;
        return errorMsg != null ? errorMsg.equals(that.errorMsg) : that.errorMsg == null;
    }

    @Override
    public int hashCode() {
        int result = getErrorCode();
        return 31 * result + (errorMsg != null ? errorMsg.hashCode() : 0);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
