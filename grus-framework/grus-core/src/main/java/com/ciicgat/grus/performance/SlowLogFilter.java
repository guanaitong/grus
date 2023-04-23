/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

import com.ciicgat.grus.core.Module;

/**
 * Created by August.Zhou on 2022/9/1 09:36
 */
public interface SlowLogFilter {

    boolean accept(Module module, long nanosDuration, String detail);

    SlowLogFilter DEFAULT = (module, nanosDuration, detail) -> Thread.currentThread().getName().startsWith("http-nio");
}
