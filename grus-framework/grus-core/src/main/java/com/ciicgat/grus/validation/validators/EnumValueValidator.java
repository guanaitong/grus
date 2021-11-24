/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.validation.validators;




import com.ciicgat.grus.validation.constraints.EnumValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * Validator自定义实现，验证是否满足给定集合
 *
 * @author wanchongyang
 * @date 2019-03-04 15:11
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {
    private String[] strValues;
    private int[] intValues;
    private boolean required;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        strValues = constraintAnnotation.strValues();
        intValues = constraintAnnotation.intValues();
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!required && value == null) {
            return true;
        }

        if (value instanceof String) {
            return Arrays.asList(strValues).contains(value);
        } else if (value instanceof Integer integerVal) {
            for (int i : intValues) {
                if (i == integerVal.intValue()) {
                    return true;
                }
            }
        }

        return false;
    }
}
