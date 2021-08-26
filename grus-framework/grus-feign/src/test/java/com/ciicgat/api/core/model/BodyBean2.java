/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.model;

import com.ciicgat.api.core.annotation.UrlFormBody;

import java.util.List;
import java.util.Objects;

/**
 * Created by August.Zhou on 2017/7/31 10:36.
 */
@UrlFormBody
public class BodyBean2 {

    String text;
    int integer;
    private List<Integer> idList;

    public BodyBean2(String text, int integer, List<Integer> idList) {
        this.text = text;
        this.integer = integer;
        this.idList = idList;
    }

    public BodyBean2() {
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

    public List<Integer> getIdList() {
        return idList;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BodyBean2 bodyBean2 = (BodyBean2) o;
        return integer == bodyBean2.integer &&
                Objects.equals(text, bodyBean2.text) &&
                Objects.equals(idList, bodyBean2.idList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, integer, idList);
    }
}
