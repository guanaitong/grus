/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.impl;

import com.ciicgat.grus.service.GrusRuntimeContext;
import com.ciicgat.grus.service.GrusService;
import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkEnv;
import com.ciicgat.sdk.util.system.WorkIdc;
import com.ciicgat.sdk.util.system.WorkRegion;

/**
 * Created by August.Zhou on 2019-03-05 14:10.
 */
public class StandardGrusRuntimeContext implements GrusRuntimeContext {
    private final long startupDate = System.currentTimeMillis();

    private final GrusService selfService;

    public StandardGrusRuntimeContext() {
        this.selfService = new GrusService(Systems.APP_NAME, "");
    }


    @Override
    public GrusService selfService() {
        return selfService;
    }

    @Override
    public String getAppName() {
        return Systems.APP_NAME;
    }

    @Override
    public String getAppSecret() {
        return Systems.APP_SECRET;
    }

    @Override
    public String getAppInstance() {
        return Systems.APP_INSTANCE;
    }

    @Override
    public String getHostName() {
        return Systems.HOST_NAME;
    }

    @Override
    public String getHostIp() {
        return Systems.HOST_IP;
    }

    @Override
    public WorkEnv getWorkEnv() {
        return WorkRegion.getCurrentWorkRegion().getWorkEnv();
    }

    @Override
    public WorkIdc getWorkIdc() {
        return WorkRegion.getCurrentWorkRegion().getWorkIdc();
    }


    @Override
    public long getStartupDate() {
        return startupDate;
    }
}
