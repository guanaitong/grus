/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet;

/**
 * @Author: Jiaju.Wei
 * @Date: Created in 2018/6/6
 * @Description:
 */
public class TestData {
    private String stringData;

    private Integer intData;

    public String getStringData() {
        return stringData;
    }

    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    public Integer getIntData() {
        return intData;
    }

    public void setIntData(Integer intData) {
        this.intData = intData;
    }

    @Override
    public String toString() {
        return "TestData{" +
                "stringData='" + stringData + '\'' +
                ", intData=" + intData +
                '}';
    }
}
