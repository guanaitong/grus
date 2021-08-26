/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.ByteRange;

/**
 * Created by Josh on 17-7-18.
 */
public class ByteRangeValidator extends Validator<ByteRange> {

    @Override
    public ValidateResult validate(ByteRange in, String paramName, Object paramValue) {

        if (!in.enable()) {
            return ValidateResult.SUCCESS;
        }

        if (paramValue != null && (paramValue.getClass() == Byte.class || paramValue.getClass() == Byte.TYPE)) {

            StringBuilder sb = new StringBuilder();
            for (byte value : in.value()) {
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
