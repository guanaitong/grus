/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.CheckHTMLTag;

import java.util.regex.Pattern;

/**
 * 默认只要是String类型的就针对脚本进行检查
 * 由于是强制检查，所以不和注解进行关联
 * Created by Andy
 */
public class CheckHTMLTagValidator extends Validator<CheckHTMLTag> {


    @Override
    public ValidateResult validate(CheckHTMLTag checkHTMLTag, String paramName, Object paramValue) {
        if (!checkHTMLTag.enable()) {
            return ValidateResult.SUCCESS;
        }

        if (paramValue instanceof String) {
            String msg = checkHTMLTag.msg();

            if (msg.isBlank()) {
                msg = "参数验证不通过, " + paramName + "的值不能包含html标签";
            }

            String paramValueStr = (String) paramValue;

            if (Pattern.compile(checkHTMLTag.value()).matcher(paramValueStr).find()) {
                return ValidateResult.error(new FailedReason(paramName, paramValue, msg, checkHTMLTag.notice()));
            }
        }
        return ValidateResult.SUCCESS;
    }
}
