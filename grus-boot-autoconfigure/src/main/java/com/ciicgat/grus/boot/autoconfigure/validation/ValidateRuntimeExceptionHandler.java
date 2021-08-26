/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation;

import com.ciicgat.boot.validator.exception.ValidateRuntimeException;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 因为ValidateRuntimeException这个类是可选依赖，有的时候classpath中不存在，所以没办法放到GlobalExceptionHandler中统一处理
 * Created by August.Zhou on 2019-05-29 10:53.
 */
@Deprecated
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE + 1)
public class ValidateRuntimeExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateRuntimeExceptionHandler.class);

    @ExceptionHandler(ValidateRuntimeException.class)
    public ApiResponse handleValidateRuntimeException(ValidateRuntimeException e) {
        LOGGER.warn("errorCode {} errorMsg {}", e.getErrorCode(), e.getErrorMsg());
        return ApiResponse.fail(e);
    }

}
