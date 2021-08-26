/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation;

import com.ciicgat.boot.validator.MethodValidator;
import com.ciicgat.boot.validator.ValidateResult;
import com.ciicgat.boot.validator.exception.ValidateRuntimeException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Josh on 17-11-9.
 */
@Deprecated
public class ValidationAroundAdvice implements MethodInterceptor {

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final ConcurrentHashMap<Method, MethodValidator> methodValidatorConcurrentHashMap = new ConcurrentHashMap<>();


    private int errorCode;

    public ValidationAroundAdvice(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Method method = invocation.getMethod();
        MethodValidator methodValidator = methodValidatorConcurrentHashMap.get(method);
        if (methodValidator == null) {
            String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
            MethodValidator newMethodValidator = new MethodValidator(method, paramNames);
            methodValidator = methodValidatorConcurrentHashMap.putIfAbsent(method, newMethodValidator);
            if (methodValidator == null) {
                methodValidator = newMethodValidator;
            }
        }
        ValidateResult validateResult = methodValidator.validate(args);

        if (!validateResult.isValid()) {
            throw new ValidateRuntimeException(errorCode, validateResult.getFailedReason());
        }
        return invocation.proceed();
    }
}
