/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.grus.logger.LogExclude;

/**
 * Created by August.Zhou on 2024/4/22 09:48
 */
@ServiceName(value = "services1", urlPathPrefix = "xx")
public interface Service1 {

    @LogExclude
    void test1();

    void test2();
}
