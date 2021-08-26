/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation;

import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static com.ciicgat.grus.boot.autoconfigure.validation.HibernateValidatorProperties.VALIDATOR_PREFIX;

/**
 * @author wanchongyang
 * @date 2020/5/7 9:24 下午
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HibernateValidatorProperties.class)
@ConditionalOnClass(HibernateValidator.class)
@AutoConfigureBefore(ValidationAutoConfiguration.class)
public class GrusValidationAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = VALIDATOR_PREFIX, value = "enabled", havingValue = "true", matchIfMissing = false)
    public Validator validator(HibernateValidatorProperties hibernateValidatorProperties) {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty("hibernate.validator.fail_fast", String.valueOf(hibernateValidatorProperties.isFailFast()))
                .buildValidatorFactory();

        return validatorFactory.getValidator();
    }

}
