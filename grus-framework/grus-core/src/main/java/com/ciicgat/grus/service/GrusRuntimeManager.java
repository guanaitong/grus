/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service;

import java.util.Map;

/**
 * Created by August.Zhou on 2019-03-06 10:12.
 */
public interface GrusRuntimeManager {

    GrusRuntimeContext getGrusRuntimeContext();

    GrusRuntimeConfig getGrusRuntimeConfig();

    boolean isHealthCheckPassed();

    /**
     * 获取当前服务的下游服务，也就是当前服务依赖的服务
     *
     * @return
     */
    Map<GrusService, GrusServiceStatus> getDownstreamService();


    GrusServiceStatus registerDownstreamService(String serviceName, String host);

    GrusServiceStatus getDownstreamServiceStatus(GrusService grusService);

    void unregisterDownstreamService(GrusService grusService);


    /**
     * 获取当前服务的上游服务，也就是依赖当前服务的服务
     *
     * @return
     */
    Map<GrusService, GrusServiceStatus> getUpstreamService();

    GrusServiceStatus registerUpstreamService(String serviceName, String host);

    GrusServiceStatus getUpstreamServiceStatus(GrusService grusService);

    void unregisterUpstreamService(GrusService grusService);

}
