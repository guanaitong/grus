/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.Json;
import com.ciicgat.boot.validator.annotation.JsonFormat;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.json.JSONException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by Josh on 17-6-10.
 */
public class JsonValidator extends Validator<Json> {

    @Override
    public ValidateResult validate(Json json, String paramName, Object paramValue) {

        if (!json.enable()) {
            return ValidateResult.SUCCESS;
        }

        if (paramValue != null &&
                (paramValue instanceof String && !"".equals(paramValue))) {

            try {
                JsonNode jsonNode = JSON.parse((String) paramValue);
                if (json.format() == JsonFormat.jsonObject) {
                    if (!jsonNode.isObject()) {
                        String msg = "参数验证不通过, " + paramName + "指定了无效的json格式";
                        return ValidateResult.error(new FailedReason(paramName, msg, json.notice()));
                    }
                } else if (json.format() == JsonFormat.jsonArray) {
                    if (!jsonNode.isArray()) {
                        String msg = "参数验证不通过, " + paramName + "指定了无效的json格式";
                        return ValidateResult.error(new FailedReason(paramName, msg, json.notice()));
                    }
                } else {
                    String msg = "参数验证不通过, " + paramName + "指定了无效的json格式";
                    return ValidateResult.error(new FailedReason(paramName, msg, json.notice()));
                }
            } catch (JSONException exception) {
                String msg = json.msg();
                if (msg.isBlank()) {
                    msg = "参数验证不通过, " + paramName + "json格式错误";
                }
                return ValidateResult.error(new FailedReason(paramName, paramValue, msg, json.notice()));
            }
        }
        return ValidateResult.SUCCESS;
    }


}
