/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.impl;

import com.ciicgat.grus.service.GrusRuntimeConfig;
import com.ciicgat.grus.service.GrusRuntimeContext;
import com.ciicgat.grus.service.GrusRuntimeManager;
import com.ciicgat.grus.service.GrusService;
import com.ciicgat.grus.service.GrusServiceStatus;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by August.Zhou on 2019-03-06 13:39.
 */
public class StandardGrusRuntimeManager implements GrusRuntimeManager {

    private GrusRuntimeContext grusRuntimeContext;

    private GrusRuntimeConfig grusRuntimeConfig = new StandardGrusRuntimeConfig();

    private ConcurrentMap<GrusService, GrusServiceStatus> downstreamServices = new ConcurrentHashMap<>();

    private ConcurrentMap<GrusService, GrusServiceStatus> upstreamServices = new ConcurrentHashMap<>();


    public StandardGrusRuntimeManager(GrusRuntimeContext grusRuntimeContext) {
        this.grusRuntimeContext = grusRuntimeContext;
    }

    public void setGrusRuntimeConfig(GrusRuntimeConfig grusRuntimeConfig) {
        this.grusRuntimeConfig = grusRuntimeConfig;
    }

    @Override
    public GrusRuntimeContext getGrusRuntimeContext() {
        return grusRuntimeContext;
    }

    @Override
    public GrusRuntimeConfig getGrusRuntimeConfig() {
        return grusRuntimeConfig;
    }

    @Override
    public boolean isHealthCheckPassed() {
        return true;
    }

    @Override
    public Map<GrusService, GrusServiceStatus> getDownstreamService() {
        return Collections.unmodifiableMap(downstreamServices);
    }

    @Override
    public GrusServiceStatus registerDownstreamService(String serviceName, String host) {
        GrusService grusService = new GrusService(serviceName, host);
        GrusServiceStatus grusServiceStatus = downstreamServices.get(grusService);
        if (grusServiceStatus != null) {
            return grusServiceStatus;
        }
        GrusServiceStatus newGrusServiceStatus = new GrusServiceStatus(grusService);
        grusServiceStatus = downstreamServices.putIfAbsent(grusService, newGrusServiceStatus);
        if (grusServiceStatus == null) {
            grusServiceStatus = newGrusServiceStatus;
        }
        return grusServiceStatus;
    }

    @Override
    public GrusServiceStatus getDownstreamServiceStatus(GrusService grusService) {
        return downstreamServices.get(grusService);
    }


    @Override
    public void unregisterDownstreamService(GrusService grusService) {
        downstreamServices.remove(grusService);
    }

    @Override
    public Map<GrusService, GrusServiceStatus> getUpstreamService() {
        return Collections.unmodifiableMap(upstreamServices);
    }

    @Override
    public GrusServiceStatus registerUpstreamService(String serviceName, String host) {
        GrusService grusService = new GrusService(serviceName, host);
        GrusServiceStatus grusServiceStatus = upstreamServices.get(grusService);
        if (grusServiceStatus != null) {
            return grusServiceStatus;
        }
        GrusServiceStatus newGrusServiceStatus = new GrusServiceStatus(grusService);
        grusServiceStatus = upstreamServices.putIfAbsent(grusService, newGrusServiceStatus);
        if (grusServiceStatus == null) {
            grusServiceStatus = newGrusServiceStatus;
        }
        return grusServiceStatus;
    }

    @Override
    public GrusServiceStatus getUpstreamServiceStatus(GrusService grusService) {
        return upstreamServices.get(grusService);
    }

    @Override
    public void unregisterUpstreamService(GrusService grusService) {
        upstreamServices.remove(grusService);
    }
}
