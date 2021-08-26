/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.hibernate;

import com.ciicgat.grus.validation.constraints.Decimal;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Stanley Shen
 * @version 1.0.0
 * @date 2019/8/12 15:48
 */
public class DecimalValidatorTest {

    @Test
    public void testScale() {
        ValidationModel model = new ValidationModel();
        model.setFoo1(BigDecimal.valueOf(9.99));
        model.setFoo2(0.01);
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<ValidationModel>> validate = validator.validate(model);
        List<String> messageList = new ArrayList<>();
        for (ConstraintViolation<ValidationModel> constraintViolation : validate) {
            messageList.add(constraintViolation.getMessage());
        }
        Assertions.assertThat(messageList).hasSize(1);
        messageList.forEach(System.out::println);

        model.setFoo1(BigDecimal.valueOf(10));
        validate = validator.validate(model);
        messageList = new ArrayList<>();
        for (ConstraintViolation<ValidationModel> constraintViolation : validate) {
            messageList.add(constraintViolation.getMessage());
        }
        Assertions.assertThat(messageList).hasSize(0);
        messageList.forEach(System.out::println);
    }

    @Test
    public void testMaxAndMin() {
        ValidationModel model = new ValidationModel();
        model.setFoo1(BigDecimal.valueOf(0));
        model.setFoo2(1000);
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<ValidationModel>> validate = validator.validate(model);
        List<String> messageList = new ArrayList<>();
        for (ConstraintViolation<ValidationModel> constraintViolation : validate) {
            String messageTemplate = constraintViolation.getMessageTemplate();
            String messageTemplate1 = constraintViolation.getConstraintDescriptor().getMessageTemplate();
            messageList.add(constraintViolation.getMessage());
        }
        Assertions.assertThat(messageList).hasSize(2);
        messageList.forEach(System.out::println);
    }

    @Test
    public void testString() {
        ValidationModel model = new ValidationModel();
        model.setFoo2(10);
        model.setFoo3("10");
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<ValidationModel>> validate = validator.validate(model);
        List<String> messageList = new ArrayList<>();
        for (ConstraintViolation<ValidationModel> constraintViolation : validate) {
            messageList.add(constraintViolation.getMessage());
        }
        Assertions.assertThat(messageList).hasSize(0);
        messageList.forEach(System.out::println);
    }

    private static class ValidationModel {

        @Decimal(scale = 1, max = 99.99, min = 0.01)
        private BigDecimal foo1;

        @Decimal(max = 99.99, min = 0.01)
        private double foo2;

        @Decimal(max = 99.99, min = 0.01)
        private String foo3;

        public BigDecimal getFoo1() {
            return foo1;
        }

        public void setFoo1(BigDecimal foo1) {
            this.foo1 = foo1;
        }

        public double getFoo2() {
            return foo2;
        }

        public void setFoo2(double foo2) {
            this.foo2 = foo2;
        }

        public String getFoo3() {
            return foo3;
        }

        public void setFoo3(String foo3) {
            this.foo3 = foo3;
        }
    }

}
