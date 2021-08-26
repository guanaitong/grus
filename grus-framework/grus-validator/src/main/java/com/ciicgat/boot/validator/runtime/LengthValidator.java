/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.Length;

/**
 * Created by Josh on 17-6-10.
 */
public class LengthValidator extends Validator<Length> {

    @Override
    public ValidateResult validate(Length length, String paramName, Object paramValue) {

        if (!length.enable()) {
            return ValidateResult.SUCCESS;
        }

        if (paramValue != null &&
                (paramValue.getClass() == String.class
                        || paramValue.getClass() == Byte.class || paramValue.getClass() == Byte.TYPE
                        || paramValue.getClass() == Short.class || paramValue.getClass() == Short.TYPE
                        || paramValue.getClass() == Integer.class || paramValue.getClass() == Integer.TYPE
                        || paramValue.getClass() == Long.class || paramValue.getClass() == Long.TYPE)) {

            String v = String.valueOf(paramValue);

            if ("".equals(paramValue) && length.skipEmpty()) {
                return ValidateResult.SUCCESS;
            }

            if (v.length() > length.max()) {
                String msg = length.msg();
                if (msg.isBlank()) {
                    msg = "参数验证不通过, " + paramName + "超过最大字符长度" + length.max();
                }
                return ValidateResult.error(new FailedReason(paramName, paramValue, msg, length.notice()));
            } else if (v.length() < length.min()) {
                String msg = length.msg();
                if (msg.isBlank()) {
                    msg = "参数验证不通过, " + paramName + "低于最小字符长度" + length.min();
                }
                return ValidateResult.error(new FailedReason(paramName, paramValue, msg, length.notice()));
            }
        }
        return ValidateResult.SUCCESS;
    }


}
