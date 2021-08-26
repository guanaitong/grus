/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service;

import java.util.concurrent.atomic.LongAdder;

/**
 * Created by August.Zhou on 2019-05-07 15:22.
 */
public class GrusServiceStatus {

    private final GrusService grusService;

    private boolean status;

    private final LongAdder succeeded = new LongAdder();

    private final LongAdder failed = new LongAdder();

    public GrusServiceStatus(GrusService grusService) {
        this.grusService = grusService;
    }

    public GrusService getGrusService() {
        return grusService;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LongAdder getSucceeded() {
        return succeeded;
    }

    public LongAdder getFailed() {
        return failed;
    }

    public void incrementSucceeded() {
        succeeded.increment();
    }

    public void incrementFailed() {
        failed.increment();
    }
}
