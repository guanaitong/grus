/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.NotEmpty;

/**
 * Created by Josh on 18-1-11.
 */
public class NotEmptyValidator extends Validator<NotEmpty> {

    @Override
    public ValidateResult validate(NotEmpty notEmpty, String paramName, Object paramValue) {

        if (!notEmpty.enable()) {
            return ValidateResult.SUCCESS;
        }

        if (paramValue == null ||
                (paramValue instanceof String
                        && notEmpty.value()
                        && ((String) paramValue).isEmpty())) {
            String msg = notEmpty.msg();
            if (msg.isBlank()) {
                msg = "参数验证不通过, " + paramName + "参数不能为空";
            }
            return ValidateResult.error(new FailedReason(paramName, paramValue, msg, notEmpty.notice()));
        }
        return ValidateResult.SUCCESS;
    }


}
