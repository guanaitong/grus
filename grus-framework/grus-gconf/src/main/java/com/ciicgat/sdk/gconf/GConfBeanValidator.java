/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

/**
 * Created by August.Zhou on 2019-01-30 13:47.
 */
public interface GConfBeanValidator {
    /**
     * Validate this bean.
     *
     * @throws RuntimeException in case of validation failure
     */
    void validate() throws RuntimeException;


}
