/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

import com.ciicgat.boot.validator.annotation.InEnum;
import com.ciicgat.boot.validator.annotation.Min;
import com.ciicgat.boot.validator.annotation.NotNull;
import com.ciicgat.boot.validator.annotation.StringRange;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Josh on 17-11-9.
 */
public class TestBean2 implements Serializable {

    @NotNull
    private String testString;

    @NotNull
    @Min(1)
    private Long testLong;

    @StringRange({"ID_ASC", "ID_DESC", "NAME_ASC", "NAME_DESC"})
    private String sortMode;

    @InEnum(source = InvoiceStatus.class, key = "code")
    private Integer invoiceStatus;

    List<TestBean3> testBean3List;

    TestBean3[] testBean3s;

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    public Long getTestLong() {
        return testLong;
    }

    public void setTestLong(Long testLong) {
        this.testLong = testLong;
    }

    public String getSortMode() {
        return sortMode;
    }

    public void setSortMode(String sortMode) {
        this.sortMode = sortMode;
    }

    public Integer getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(Integer invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public List<TestBean3> getTestBean3List() {
        return testBean3List;
    }

    public void setTestBean3List(List<TestBean3> testBean3List) {
        this.testBean3List = testBean3List;
    }

    public TestBean3[] getTestBean3s() {
        return testBean3s;
    }

    public void setTestBean3s(TestBean3[] testBean3s) {
        this.testBean3s = testBean3s;
    }
}
