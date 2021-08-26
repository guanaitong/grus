/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

/**
 * Created by Josh on 17-6-9.
 */
public class ValidateResult {

    private boolean valid = false;
    private FailedReason failedReason;
    public static final ValidateResult SUCCESS = new ValidateResult(true);

    private ValidateResult() {

    }

    private ValidateResult(boolean valid) {
        this.valid = valid;
    }

    private ValidateResult(boolean valid, FailedReason failedReason) {
        this.valid = valid;
        this.failedReason = failedReason;
    }

    public static ValidateResult error(FailedReason failedReason) {
        return new ValidateResult(false, failedReason);
    }

    public boolean isValid() {
        return valid;
    }

    public FailedReason getFailedReason() {
        return failedReason;
    }

}
