/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.validate;

public enum InvoiceStatus {

    //1、待确认 2、待审核 3、已通过 4、未通过 5、已取消--开票系统
    VERIFY(1, "待确认"),
    AUDIT(2, "待审核"),
    PASS(3, "已通过"),
    NOT_PASS(4, "未通过"),
    CANCEL(5, "已取消");


    private int code;
    private String msg;

    InvoiceStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
