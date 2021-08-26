/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.web;

import com.ciicgat.grus.service.GrusFramework;
import com.ciicgat.grus.service.GrusRuntimeManager;
import com.ciicgat.grus.service.GrusServiceStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2019-06-25 12:45.
 */
@RestController
@RequestMapping("/grus")
public class GrusSystemController {

    @RequestMapping("/dep")
    public Map<String, Collection<GrusServiceStatus>> dependency() {
        GrusRuntimeManager grusRuntimeManager = GrusFramework.getGrusRuntimeManager();
        Map<String, Collection<GrusServiceStatus>> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("down", grusRuntimeManager.getDownstreamService().values());
        linkedHashMap.put("up", grusRuntimeManager.getUpstreamService().values());
        return linkedHashMap;
    }

    @RequestMapping("/dep/down")
    public Collection<GrusServiceStatus> dependencyDown() {
        GrusRuntimeManager grusRuntimeManager = GrusFramework.getGrusRuntimeManager();
        return grusRuntimeManager.getDownstreamService().values();
    }

    @RequestMapping("/dep/up")
    public Collection<GrusServiceStatus> dependencyUp() {
        GrusRuntimeManager grusRuntimeManager = GrusFramework.getGrusRuntimeManager();
        return grusRuntimeManager.getUpstreamService().values();
    }

}
