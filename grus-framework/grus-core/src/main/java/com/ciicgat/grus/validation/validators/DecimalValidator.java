/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.validation.validators;

import com.ciicgat.grus.validation.constraints.Decimal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * @author Stanley Shen
 * @version 1.0.0
 * @date 2019/8/12 11:09
 */
public class DecimalValidator implements ConstraintValidator<Decimal, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecimalValidator.class);

    private int scale;
    private double min;
    private double max;

    @Override
    public void initialize(Decimal constraintAnnotation) {
        scale = constraintAnnotation.scale();
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        BigDecimal input = null;
        if (value instanceof Double || value instanceof Float
                || value.getClass().equals(double.class) || value.getClass().equals(float.class)) {
            input = new BigDecimal(value.toString());
        }
        if (value instanceof BigDecimal) {
            input = (BigDecimal) value;
        }
        if (input == null) {
            LOGGER.warn("该注解只支持BigDecimal, Double和Float类型, 请勿使用在别的类型参数上");
            return true;
        }

        if (input.compareTo(BigDecimal.ZERO) != 0 && input.scale() > scale) {
            LOGGER.warn("精度大于{}", scale);
            if (StringUtils.isBlank(context.getDefaultConstraintMessageTemplate())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("精度不能大于" + scale).addConstraintViolation();
            }
            return false;
        }

        if (input.doubleValue() > max) {
            LOGGER.warn("金额大于{}", max);
            if (StringUtils.isBlank(context.getDefaultConstraintMessageTemplate())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("金额不能大于" + max).addConstraintViolation();
            }
            return false;
        }

        if (input.doubleValue() < min) {
            LOGGER.warn("金额小于{}", min);
            if (StringUtils.isBlank(context.getDefaultConstraintMessageTemplate())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("金额不能小于" + min).addConstraintViolation();
            }
            return false;
        }

        return true;
    }

}
