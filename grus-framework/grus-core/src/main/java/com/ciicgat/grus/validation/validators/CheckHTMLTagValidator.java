/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.validation.validators;


import com.ciicgat.grus.validation.constraints.CheckHTMLTag;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validator自定义实现，验证字符串是否包含html标签, 支持覆写正则
 *
 * @author wanchongyang
 * @date 2019-05-16 15:57
 */
public class CheckHTMLTagValidator implements ConstraintValidator<CheckHTMLTag, String> {

    private Pattern pattern;

    @Override
    public void initialize(CheckHTMLTag constraintAnnotation) {
        pattern = Pattern.compile(constraintAnnotation.pattern());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            try {
                if (pattern.matcher(value).find()) {
                    return false;
                }
            } catch (Exception ex) {
                // no-ops
                return false;
            }
        }

        return true;
    }
}
