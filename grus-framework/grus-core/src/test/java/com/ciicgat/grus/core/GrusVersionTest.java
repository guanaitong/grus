/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GrusVersionTest {
    @Test
    public void test() {
        String version = GrusVersion.getVersion();
        Assertions.assertNotEquals(version, null);
    }
}
