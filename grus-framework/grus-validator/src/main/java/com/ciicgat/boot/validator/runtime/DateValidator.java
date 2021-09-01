/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;


import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.Date;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;


/**
 * Created by Josh on 17-6-10.
 */

/**
 * 只针对 String 类型的字段验证
 */
public class DateValidator extends Validator<Date> {


    @Override
    public ValidateResult validate(Date date, String paramName, Object paramValue) {
        if (!date.enable()) {
            return ValidateResult.SUCCESS;
        }

        if (paramValue instanceof String) {

            String value = (String) paramValue;

            if (!"".equals(value)) {
                try {
                    DateUtils.parseDate(value, date.format());
                } catch (ParseException e) {
                    String msg = date.msg();
                    if (msg.isBlank()) {
                        msg = "参数验证不通过, " + paramName + "日期格式错误, 需要" + date.format() + "的日期格式";
                    }
                    return ValidateResult.error(new FailedReason(paramName, paramValue, msg, date.notice()));
                }
            }
        }
        return ValidateResult.SUCCESS;
    }

}
