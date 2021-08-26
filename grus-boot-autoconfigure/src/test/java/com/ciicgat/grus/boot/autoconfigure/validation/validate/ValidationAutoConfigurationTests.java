/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.validate;

import com.ciicgat.grus.boot.autoconfigure.validation.ValidationAroundAdvice;
import com.ciicgat.grus.boot.autoconfigure.validation.ValidationAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.validation.ValidationPointcutAdvisor;
import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August.Zhou on 2019-04-08 13:44.
 */

public class ValidationAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(AopAutoConfiguration.class, ValidationAutoConfiguration.class);

    @Test
    public void test() {
        this.contextRunner
                .withPropertyValues("spring.application.name=grus-demo", "grus.validation.errorCode=99999", "grus.validation.pointCut=@annotation(com.ciicgat.grus.boot.autoconfigure.gconf.GConfBean)")
                .run(context -> {
                    ValidationAroundAdvice aroundAdvice = context.getBean(ValidationAroundAdvice.class);
                    assertThat(aroundAdvice).isInstanceOf(ValidationAroundAdvice.class);
                    AspectJExpressionPointcut aspectJExpressionPointcut = context.getBean(AspectJExpressionPointcut.class);
                    assertThat(aspectJExpressionPointcut).isInstanceOf(AspectJExpressionPointcut.class);
                    ValidationPointcutAdvisor validationPointcutAdvisor = context.getBean(ValidationPointcutAdvisor.class);
                    assertThat(validationPointcutAdvisor).isInstanceOf(ValidationPointcutAdvisor.class);
                });
    }

}
