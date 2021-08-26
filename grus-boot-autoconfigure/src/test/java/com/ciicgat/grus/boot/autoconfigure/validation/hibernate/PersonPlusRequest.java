/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.hibernate;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

/**
 * @author wanchongyang
 * @date 2020/5/9 11:28 下午
 */
public class PersonPlusRequest {
    @NotBlank
    @URL(regexp = "^(https?)://[^\\s]*")
    private String portrait;

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
