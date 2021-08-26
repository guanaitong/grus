/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.model;

import com.ciicgat.api.core.annotation.UrlFormBody;

/**
 * Created by August.Zhou on 2017/7/31 10:36.
 */
@UrlFormBody
public class BodyBean {

    String text;
    int integer;

    public BodyBean(String text, int integer) {
        this.text = text;
        this.integer = integer;
    }

    public BodyBean() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BodyBean)) return false;

        BodyBean testBean = (BodyBean) o;

        if (integer != testBean.integer) return false;
        return text != null ? text.equals(testBean.text) : testBean.text == null;
    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + integer;
        return result;
    }

    @Override
    public String toString() {
        return "BodyBean{" +
                "text='" + text + '\'' +
                ", integer=" + integer +
                '}';
    }
}
