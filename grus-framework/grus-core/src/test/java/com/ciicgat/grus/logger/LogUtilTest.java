/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.logger;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by August.Zhou on 2022/10/11 13:44
 */
class LogUtilTest {

    @Test
    void truncate() {
        Assertions.assertNull(LogUtil.truncate(null));
        Assertions.assertEquals("123", LogUtil.truncate("123"));
        String resp = StringUtils.repeat('a', 10000);
        Assertions.assertEquals(2000, LogUtil.truncate(resp).length());
    }
}
