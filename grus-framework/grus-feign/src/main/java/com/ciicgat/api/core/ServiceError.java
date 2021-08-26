/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import feign.FeignException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * Created by August.Zhou on 2018-10-26 13:29.
 */
public enum ServiceError {
    STATUS_CODE_NOT_OK("服务返回状态码错误"),
    UN_KNOWN_HOST("服务DNS解析失败"),
    TIMEOUT("服务超时"),
    CONNECT_FAILED("服务连接异常"),
    ILLEGAL_ARGUMENT("非法请求参数"),
    UNKNOWN("服务网络异常"),;

    final String desc;

    ServiceError(String desc) {
        this.desc = Objects.requireNonNull(desc);
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return desc;
    }

    public static ServiceError valueOf(FeignException feignException) {
        int status = feignException.status();
        if (status == 0) {
            Throwable cause = feignException.getCause();
            if (cause instanceof UnknownHostException) {
                return ServiceError.UN_KNOWN_HOST;
            } else if (cause instanceof SocketTimeoutException) {
                return ServiceError.TIMEOUT;
            } else if (cause instanceof ConnectException) {
                return ServiceError.CONNECT_FAILED;
            } else if (cause instanceof IllegalArgumentException) {
                return ServiceError.ILLEGAL_ARGUMENT;
            }
            return ServiceError.UNKNOWN;
        }
        return ServiceError.STATUS_CODE_NOT_OK;
    }
}
