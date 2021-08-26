/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.NotNull;

/**
 * Created by Josh on 17-6-10.
 */
public class NotNullValidator extends Validator<NotNull> {

    @Override
    public ValidateResult validate(NotNull notNull, String paramName, Object paramValue) {

        if (!notNull.enable()) {
            return ValidateResult.SUCCESS;
        }

        if (notNull.value() && paramValue == null) {
            String msg = notNull.msg();
            if (msg.isBlank()) {
                msg = "参数验证不通过, " + paramName + " 不能为空";
            }
            return ValidateResult.error(new FailedReason(paramName, paramValue, msg, notNull.notice()));
        }
        return ValidateResult.SUCCESS;
    }


}
