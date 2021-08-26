/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;


/**
 * Created by Josh on 17-11-9.
 */
@Deprecated
public class ValidationPointcutAdvisor extends AbstractPointcutAdvisor {

    private final AspectJExpressionPointcut aspectJExpressionPointcut;

    private final ValidationAroundAdvice aroundAdvice;

    public ValidationPointcutAdvisor(AspectJExpressionPointcut aspectJExpressionPointcut, ValidationAroundAdvice aroundAdvice) {
        this.aspectJExpressionPointcut = aspectJExpressionPointcut;
        this.aroundAdvice = aroundAdvice;
    }

    @Override
    public Pointcut getPointcut() {
        return this.aspectJExpressionPointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.aroundAdvice;
    }
}
