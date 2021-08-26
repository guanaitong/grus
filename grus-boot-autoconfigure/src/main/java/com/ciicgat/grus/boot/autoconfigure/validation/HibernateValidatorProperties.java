/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.ciicgat.grus.boot.autoconfigure.validation.HibernateValidatorProperties.VALIDATOR_PREFIX;

/**
 * @author wanchongyang
 * @date 2020/5/7 9:16 下午
 * @see org.hibernate.validator.HibernateValidatorConfiguration
 */
@ConfigurationProperties(prefix = VALIDATOR_PREFIX)
public class HibernateValidatorProperties {
    public static final String VALIDATOR_PREFIX = "grus.hibernate.validator";

    /**
     * 是否开启，默认false
     */
    private boolean enabled = false;

    /**
     * 快速失败，默认true（hibernate validator默认false）
     */
    private boolean failFast = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFailFast() {
        return failFast;
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }
}
