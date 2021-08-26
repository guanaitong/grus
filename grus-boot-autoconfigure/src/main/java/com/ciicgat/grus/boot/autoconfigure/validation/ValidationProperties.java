/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Created by Josh on 17-11-9.
 */
@Deprecated
@ConfigurationProperties(prefix = "grus.validation")
@Validated
public class ValidationProperties {

    /**
     * 参数错误码
     */
    @NotNull
    private Integer errorCode;

    /**
     * 生效的切面
     */
    private String pointCut = "@annotation(org.springframework.web.bind.annotation.RequestMapping)";

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getPointCut() {
        return pointCut;
    }

    public void setPointCut(String pointCut) {
        this.pointCut = pointCut;
    }
}
