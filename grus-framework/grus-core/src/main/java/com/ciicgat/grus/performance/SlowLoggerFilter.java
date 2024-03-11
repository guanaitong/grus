/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

/**
 * Created by August.Zhou on 2023/12/6 14:52
 */
public interface SlowLoggerFilter {

    /**
     * 返回true时，发送通知
     *
     * @param slowLog
     * @return
     */
    boolean accept(SlowLog slowLog);

    SlowLoggerFilter ALL = tags -> true;
}
