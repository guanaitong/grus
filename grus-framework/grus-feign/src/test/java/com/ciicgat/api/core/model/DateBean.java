/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.model;

import com.ciicgat.api.core.annotation.UrlFormBody;

import java.util.Date;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/3 17:31
 * @Description:
 */
@UrlFormBody
public class DateBean {

    String text;
    Date date;

    public DateBean(String text, Date date) {
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
