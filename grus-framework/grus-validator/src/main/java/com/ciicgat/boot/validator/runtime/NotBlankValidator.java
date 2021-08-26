/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.NotBlank;

/**
 * Created by Josh on 17-6-10.
 */
public class NotBlankValidator extends Validator<NotBlank> {

    @Override
    public ValidateResult validate(NotBlank notBlank, String paramName, Object paramValue) {

        if (!notBlank.enable()) {
            return ValidateResult.SUCCESS;
        }

        if (paramValue == null ||
                (paramValue instanceof String
                        && notBlank.value()
                        && ((String) paramValue).isBlank())) {
            String msg = notBlank.msg();
            if (msg.isBlank()) {
                msg = "参数验证不通过, " + paramName + "参数不能为空";
            }
            return ValidateResult.error(new FailedReason(paramName, paramValue, msg, notBlank.notice()));
        }
        return ValidateResult.SUCCESS;
    }


}
