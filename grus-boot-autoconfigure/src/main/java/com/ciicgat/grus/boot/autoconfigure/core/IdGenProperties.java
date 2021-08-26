/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/31 17:35
 * @Description: 序列生成器需要使用到的配置
 */
@ConfigurationProperties(prefix = "grus.idgen")
@Validated
public class IdGenProperties {

    /**
     * 序列生成中日期的格式，默认yyyyMMdd
     */
    @NotEmpty
    private String dateFormat = "yyyyMMdd";

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
