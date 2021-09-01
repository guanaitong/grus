/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator.runtime;

import com.ciicgat.boot.validator.FailedReason;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.Validator;
import com.ciicgat.boot.validator.annotation.InEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by City Mo on 2017/12/11.
 */
public class InEnumValidator extends Validator<InEnum> {

    public static final Logger LOGGER = LoggerFactory.getLogger(InEnumValidator.class);
    static Set<Class> set = new HashSet<>();

    @Override
    public ValidateResult validate(InEnum inEnum, String paramName, Object paramValue) {
        if (paramValue == null) {
            return ValidateResult.SUCCESS;
        }
        if (!SupportEnumType.support(paramValue.getClass())) {
            LOGGER.warn("不支持的类型:{}", paramValue.getClass());
        }

        Class<? extends Enum> clazz = inEnum.source();
        StringBuilder sb = new StringBuilder();
        boolean validate = false;
        String key = inEnum.key();
        if (!key.isBlank()) {
            boolean useGetter = inEnum.useGet();
            Method method = null;
            if (useGetter) {
                String methodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
                method = SupportEnumType.getMethod(clazz, methodName);
            } else {
                method = SupportEnumType.getMethod(clazz, key);
            }
            if (method == null) {
                throw new RuntimeException("加载枚举校验器失败");
            }

            for (Object obj : clazz.getEnumConstants()) {
                try {
                    Object value = method.invoke(obj);
                    sb.append(value).append(',');
                    if (!validate) {
                        boolean result = paramValue.equals(value);
                        if (result) {
                            validate = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("枚举校验器执行失败");
                }
            }
        } else {
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                sb.append(field.getName()).append(',');
                if (field.getName().equals(paramValue)) {
                    validate = true;
                    break;
                }
            }
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        if (!validate) {
            String msg = "参数验证不通过, " + paramName + "无效的值, 可接受的取值有:" + sb;
            return ValidateResult.error(new FailedReason(paramName, paramValue, msg, ""));
        }
        return ValidateResult.SUCCESS;
    }


}
