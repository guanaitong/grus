/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.validate;

import com.ciicgat.boot.validator.annotation.InEnum;
import com.ciicgat.boot.validator.annotation.Min;
import com.ciicgat.boot.validator.annotation.NotNull;
import com.ciicgat.boot.validator.annotation.StringRange;
import com.ciicgat.boot.validator.annotation.Valid;

import java.io.Serializable;

/**
 * Created by Josh on 17-11-9.
 */
@Valid
public class Test implements Serializable {

    @NotNull
    private String testString;

    @NotNull
    @Min(1)
    private Long testLong;

    @StringRange({"ID_ASC,ID_DESC,NAME_ASC,NAME_DESC"})
    private String sortMode;

    @InEnum(source = InvoiceStatus.class, key = "code")
    private Integer invoiceStatus;

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
}
