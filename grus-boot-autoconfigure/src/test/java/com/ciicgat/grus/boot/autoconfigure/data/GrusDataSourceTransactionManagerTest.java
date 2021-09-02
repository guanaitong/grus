/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by August.Zhou on 2021/9/2 16:14.
 */
class GrusDataSourceTransactionManagerTest {

    @Test
    void doRollback() {
        try {
            new GrusDataSourceTransactionManager(null).doRollback(null);
            fail();
        } catch (Exception e) {
        }
    }
}
