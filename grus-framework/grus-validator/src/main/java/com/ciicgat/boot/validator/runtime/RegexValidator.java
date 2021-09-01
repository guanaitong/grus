/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.Regex;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Josh on 17-6-10.
 */
public class RegexValidator extends Validator<Regex> {

    private ConcurrentMap<String, Pattern> patterns = new ConcurrentHashMap<>();

    @Override
    public ValidateResult validate(Regex regex, String paramName, Object paramValue) {

        if (!regex.enable()) {
            return ValidateResult.SUCCESS;
        }
        if (paramValue instanceof String) {
            if ("".equals(paramValue) && regex.skipEmpty()) {
                return ValidateResult.SUCCESS;
            }
            String reg = regex.value();
            if (reg.isBlank()) {
                String msg = "参数验证不通过, " + paramName + "指定的正则表达式为空";
                return ValidateResult.error(new FailedReason(paramName, paramValue, msg, regex.notice()));
            }
            try {
                Pattern pattern = getPattern(reg);
                Matcher matcher = pattern.matcher((String) paramValue);
                if (!matcher.matches()) {
                    String msg = regex.msg();
                    if (msg.isBlank()) {
                        msg = "参数验证不通过, " + paramName + "的正则不匹配, 正则" + reg;
                    }
                    return ValidateResult.error(new FailedReason(paramName, paramValue, msg, regex.notice()));
                }
            } catch (PatternSyntaxException ex) {
                String msg = "参数验证不通过, " + paramName + " 正则表达式错误";
                return ValidateResult.error(new FailedReason(paramName, paramValue, msg, regex.notice()));
            }
        }
        return ValidateResult.SUCCESS;
    }

    private Pattern getPattern(String reg) {
        Pattern pattern = patterns.get(reg);
        if (pattern != null) {
            return pattern;
        }
        pattern = Pattern.compile(reg);
        patterns.put(reg, pattern);
        return pattern;
    }


}
