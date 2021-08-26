/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation;

import com.ciicgat.boot.validator.RootObjectValidator;
import com.ciicgat.boot.validator.Validator;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Created by Josh on 17-11-9.
 */
@Deprecated
@EnableConfigurationProperties(ValidationProperties.class)
@ConditionalOnProperty(prefix = "grus.validator", value = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass({Pointcut.class, Validator.class})
@AutoConfigureAfter({AopAutoConfiguration.class})
@Configuration(proxyBeanMethods = false)
@Import(ValidateRuntimeExceptionHandler.class)
public class ValidationAutoConfiguration {

    private ValidationProperties validationProperties;

    public ValidationAutoConfiguration(ValidationProperties validationProperties) {
        this.validationProperties = validationProperties;
    }

    @Bean
    public ValidationAroundAdvice validationAroundAdvice() {
        return new ValidationAroundAdvice(validationProperties.getErrorCode());
    }

    @Bean
    public AspectJExpressionPointcut aspectJExpressionPointcut() {
        var aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(validationProperties.getPointCut());
        return aspectJExpressionPointcut;
    }


    @Bean
    public ValidationPointcutAdvisor validatorPointcutAdvisor(ValidationAroundAdvice validationAroundAdvice, AspectJExpressionPointcut aspectJExpressionPointcut) {
        return new ValidationPointcutAdvisor(aspectJExpressionPointcut, validationAroundAdvice);
    }


    @Bean({"objectValidator", "rootObjectValidator"})
    public RootObjectValidator objectValidator() {
        return new RootObjectValidator(validationProperties.getErrorCode());
    }


}
