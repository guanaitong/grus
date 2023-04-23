/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import java.util.Date;
import java.util.Objects;

/**
 * Created by August.Zhou on 2022/7/4 13:40
 */
public class TestData implements Comparable<TestData> {
    private String text;
    private Integer i;
    private Date date;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestData testData = (TestData) o;
        return Objects.equals(text, testData.text) && Objects.equals(i, testData.i) && Objects.equals(date, testData.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, i, date);
    }

    @Override
    public String toString() {
        return "TestData{" +
                "text='" + text + '\'' +
                ", i=" + i +
                ", date=" + date +
                '}';
    }

    @Override
    public int compareTo(TestData o) {
        return this.toString().compareTo(o.toString());
    }
}
