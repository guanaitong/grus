/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.validation.validators;

import com.ciicgat.grus.validation.constraints.DateStr;
import org.apache.commons.lang3.time.DateUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator自定义实现，验证是否是Date字符串
 *
 * @author wanchongyang
 * @date 2019-05-16 15:57
 */
public class DateStrValidator implements ConstraintValidator<DateStr, String> {
    private String format;

    @Override
    public void initialize(DateStr constraintAnnotation) {
        format = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            try {
                DateUtils.parseDate(value, format);
            } catch (Exception ex) {
                // no-ops
                return false;
            }
        }

        return true;
    }
}
