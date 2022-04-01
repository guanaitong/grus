/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.endpoint;

import com.ciicgat.grus.service.GrusFramework;
import com.ciicgat.grus.service.GrusRuntimeManager;
import com.ciicgat.grus.service.GrusServiceStatus;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author andy
 */
@Endpoint(id = "dep")
public class GrusServiceDependencyEndpoint {
    @ReadOperation
    public Map<String, Collection<GrusServiceStatus>> dependency() {
        GrusRuntimeManager grusRuntimeManager = GrusFramework.getGrusRuntimeManager();
        Map<String, Collection<GrusServiceStatus>> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("down", grusRuntimeManager.getDownstreamService().values());
        linkedHashMap.put("up", grusRuntimeManager.getUpstreamService().values());
        return linkedHashMap;
    }
}
