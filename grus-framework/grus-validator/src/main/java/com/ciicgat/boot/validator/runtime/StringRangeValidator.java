/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.StringRange;

/**
 * Created by Josh on 17-6-10.
 */
public class StringRangeValidator extends Validator<StringRange> {

    @Override
    public ValidateResult validate(StringRange in, String paramName, Object paramValue) {

        if (!in.enable()) {
            return ValidateResult.SUCCESS;
        }

        if (paramValue instanceof String && !"".equals(paramValue)) {
            StringBuilder sb = new StringBuilder();
            for (String value : in.value()) {
                if (value.equals(paramValue)) {
                    return ValidateResult.SUCCESS;
                }
                sb.append(value).append(',');
            }
            String msg = in.msg();
            String allowValue = sb.deleteCharAt(sb.length() - 1).toString();
            if (msg.isBlank()) {
                msg = "参数验证不通过, " + paramName + "无效的值, 可接受的取值有" + allowValue;
            }
            return ValidateResult.error(new FailedReason(paramName, paramValue, msg, in.notice()));
        }
        return ValidateResult.SUCCESS;
    }

}
