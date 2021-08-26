/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.excel;

/**
 * @Auther: chunhong.wan
 * @Date: 2020/12/10
 * @Description:
 */
public enum ExcelType {
    XLS("xls"),
    XLSX("xlsx");
    private String value;

    ExcelType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
