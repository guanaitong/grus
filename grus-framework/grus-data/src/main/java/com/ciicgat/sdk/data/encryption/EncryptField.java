/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.data.encryption;

import java.io.Serializable;

public class EncryptField implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String value;

    public EncryptField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
