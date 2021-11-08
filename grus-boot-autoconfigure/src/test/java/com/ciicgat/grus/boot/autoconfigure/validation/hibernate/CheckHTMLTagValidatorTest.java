/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.hibernate;

import com.ciicgat.grus.validation.constraints.CheckHTMLTag;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Stanley Shen
 * @version 1.0.0
 * @date 2019/8/12 14:14
 */
public class CheckHTMLTagValidatorTest {

    @Test
    public void testValidator() {
        ValidationModel model = new ValidationModel();
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<ValidationModel>> validate = validator.validate(model);
        List<String> messageList = new ArrayList<>();
        for (ConstraintViolation<ValidationModel> constraintViolation : validate) {
            messageList.add(constraintViolation.getMessage());
        }

        Assertions.assertThat(messageList.stream().map("请输入正确的信息"::equals)).hasSize(2);
        messageList.forEach(System.out::println);
    }

    private static class ValidationModel {

        @CheckHTMLTag(message = "请输入正确的信息")
        private String foo = "<script>alert(1)</script>";

        @CheckHTMLTag
        private String bar = "<你好";

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }
    }

}
