/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.util;

import com.ciicgat.grus.function.Procedure;
import com.ciicgat.sdk.lang.convert.ErrorCode;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * 断言类，抛出 {@link BusinessRuntimeException}
 *
 * @author Cliver Yuan
 * @author Stanley Shen
 * @date 2020-11-13 14:07
 */
public class BizAssert {

    private BizAssert() {
    }

    public static void state(boolean expression, ErrorCode errorCode) {
        state(expression, errorCode.getErrorCode(), errorCode.getErrorMsg(), null);
    }

    public static void state(boolean expression, ErrorCode errorCode, Procedure procedure) {
        state(expression, errorCode.getErrorCode(), errorCode.getErrorMsg(), procedure);
    }

    public static void state(boolean expression, int code, String msg) {
        state(expression, code, msg, null);
    }

    public static void state(boolean expression, int code, String msg, Procedure procedure) {
        if (!expression) {
            if (procedure != null) {
                procedure.run();
            }

            throw new BusinessRuntimeException(code, msg);
        }
    }

    public static void isTrue(boolean expression, ErrorCode errorCode) {
        isTrue(expression, errorCode, null);
    }

    public static void isTrue(boolean expression, ErrorCode errorCode, Procedure procedure) {
        state(expression, errorCode, procedure);
    }

    public static void isNull(Object object, ErrorCode errorCode) {
        isNull(object, errorCode, null);
    }

    public static void isNull(Object object, ErrorCode errorCode, Procedure procedure) {
        state(Objects.isNull(object), errorCode, procedure);
    }

    public static void notNull(Object object, ErrorCode errorCode) {
        notNull(object, errorCode, null);
    }

    public static void notNull(Object object, ErrorCode errorCode, Procedure procedure) {
        state(Objects.nonNull(object), errorCode, procedure);
    }

    public static void notEmpty(Collection<?> collection, ErrorCode errorCode) {
        notEmpty(collection, errorCode, null);
    }

    public static void notEmpty(Collection<?> collection, ErrorCode errorCode, Procedure procedure) {
        state(collection != null && !collection.isEmpty(), errorCode, procedure);
    }

    public static void notBlank(String string, ErrorCode errorCode) {
        notBlank(string, errorCode, null);
    }

    public static void notBlank(String string, ErrorCode errorCode, Procedure procedure) {
        state(StringUtils.isNotBlank(string), errorCode, procedure);
    }
}
