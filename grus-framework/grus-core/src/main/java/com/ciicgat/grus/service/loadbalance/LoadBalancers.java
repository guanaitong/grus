/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.loadbalance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by August.Zhou on 2021/9/1 15:00.
 */
public class LoadBalancers {
    private static LoadBalancer defaultLoadBalancer = new RandomLoadBalancer();
    private static final Map<String, LoadBalancer> MAP;

    static {
        Map<String, LoadBalancer> loadBalancerMap = new HashMap<>(2);
        put(loadBalancerMap, defaultLoadBalancer);
        put(loadBalancerMap, new RandomLoadBalancer());
        MAP = Collections.unmodifiableMap(loadBalancerMap);
    }

    private static void put(Map<String, LoadBalancer> loadBalancerMap, LoadBalancer loadBalancer) {
        loadBalancerMap.put(loadBalancer.getName(), loadBalancer);
    }

    public static LoadBalancer getLoadBalancer(String name) {
        return MAP.getOrDefault(name, defaultLoadBalancer);
    }
}
