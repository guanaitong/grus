/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.hibernate;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wanchongyang
 * @date 2020/5/10 9:10 下午
 */
@Service
@Validated
public class ValidateService {
    public void save(@NotNull Integer personId, @NotBlank @Length(min = 5, max = 50) String personName) {
        // no-ops
    }
}
