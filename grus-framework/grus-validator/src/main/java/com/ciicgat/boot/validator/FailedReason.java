/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;


import com.ciicgat.grus.json.JSON;

/**
 * @author Josh
 * @deprecated please not use ciicgat-validator
 */
@Deprecated
public class FailedReason {

    private String fieldName;
    private Object fieldValue;
    private String reason;
    private String notice;

    public FailedReason() {
    }

    public FailedReason(String fieldName, String reason) {
        this.fieldName = fieldName;
        this.reason = reason;
    }

    public FailedReason(String fieldName, Object fieldValue, String reason) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.reason = reason;
    }

    public FailedReason(String fieldName, Object fieldValue, String reason, String notice) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.reason = reason;
        this.notice = notice;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FailedReason setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getReason() {
        return reason;
    }

    public FailedReason setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public String getNotice() {
        return notice;
    }

    public FailedReason setNotice(String notice) {
        this.notice = notice;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
