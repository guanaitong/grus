/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.LongRange;

/**
 * Created by Josh on 17-7-18.
 */
public class LongRangeValidator extends Validator<LongRange> {

    @Override
    public ValidateResult validate(LongRange in, String paramName, Object paramValue) {

        if (!in.enable()) {
            return ValidateResult.SUCCESS;
        }

        if (paramValue != null && (paramValue.getClass() == Long.class || paramValue.getClass() == Long.TYPE)) {

            StringBuilder sb = new StringBuilder();
            for (long value : in.value()) {
                if (paramValue.equals(value)) {
                    return ValidateResult.SUCCESS;
                }
                sb.append(value).append(",");
            }
            String msg = in.msg();
            String allowedValue = sb.deleteCharAt(sb.length() - 1).toString();
            if (msg.isBlank()) {
                msg = "参数验证不通过, " + paramName + "无效的值, 可接受的取值有" + allowedValue;
            }
            return ValidateResult.error(new FailedReason(paramName, paramValue, msg, in.notice()));
        }
        return ValidateResult.SUCCESS;
    }


}
