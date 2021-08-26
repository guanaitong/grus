/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.Min;

/**
 * Created by Josh on 17-6-10.
 */
public class MinValidator extends Validator<Min> {

    @Override
    public ValidateResult validate(Min min, String paramName, Object paramValue) {

        if (!min.enable()) {
            return ValidateResult.SUCCESS;
        }

        if (paramValue != null) {
            Class<?> valueClass = paramValue.getClass();
            if ((valueClass == Byte.class || valueClass == Byte.TYPE)
                    || (valueClass == Short.class || valueClass == Short.TYPE)
                    || (valueClass == Integer.class || valueClass == Integer.TYPE)
                    || (valueClass == Long.class || valueClass == Long.TYPE)) {
                Long value = Long.valueOf(paramValue.toString());
                if (value < min.value()) {
                    String msg = min.msg();
                    if (msg.isBlank()) {
                        msg = "参数验证不通过, " + paramName + "小于最小值" + min.value();
                    }
                    return ValidateResult.error(new FailedReason(paramName, paramValue, msg, min.notice()));
                }
            }
        }
        return ValidateResult.SUCCESS;
    }

}
