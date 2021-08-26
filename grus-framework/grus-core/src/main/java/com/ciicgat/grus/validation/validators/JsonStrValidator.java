/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.validation.validators;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.json.JSONException;
import com.ciicgat.grus.validation.constraints.JsonStr;
import com.fasterxml.jackson.databind.JsonNode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator自定义实现，验证是否是JSON字符串
 *
 * @author wanchongyang
 * @date 2019-04-11 17:12
 */
public class JsonStrValidator implements ConstraintValidator<JsonStr, String> {
    private boolean isArray;

    @Override
    public void initialize(JsonStr constraintAnnotation) {
        // nothing
        isArray = constraintAnnotation.isArray();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            try {
                JsonNode jsonNode = JSON.parse(value);
                if (isArray) {
                    return jsonNode.isArray();
                }
            } catch (JSONException e) {
                // nothing
                return false;
            }
        }

        return true;
    }
}
