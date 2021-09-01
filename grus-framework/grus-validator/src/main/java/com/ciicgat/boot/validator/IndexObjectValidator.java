/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.boot.validator;

/**
 * Created by August.Zhou on 2021/9/1 15:40.
 */
public class IndexObjectValidator {
    private final ObjectValidator objectValidator;
    private final int index;

    IndexObjectValidator(ObjectValidator objectValidator, int index) {
        this.objectValidator = objectValidator;
        this.index = index;
    }

    public ObjectValidator getObjectValidator() {
        return objectValidator;
    }

    public int getIndex() {
        return index;
    }
}
