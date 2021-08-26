/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.loadbalance;

import com.ciicgat.grus.service.discovery.ServiceInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 一个简易的负载均衡实现
 *
 * @Author: August
 * @Date: 2021/7/12 12:52
 */
public abstract class LoadBalancer {
    private final String name;

    LoadBalancer(final String name) {
        this.name = name;
    }

    public ServiceInstance select(List<ServiceInstance> serviceInstanceList, Set<ServiceInstance> fails) {
        int size = serviceInstanceList.size();
        if (size == 1) {
            return serviceInstanceList.get(0);
        }
        int failSize = fails.size();
        if (failSize > 0 && failSize != size) { //优先选择非失败节点
            List<ServiceInstance> lefts = new ArrayList<>();
            for (ServiceInstance serviceInstance : serviceInstanceList) {
                if (!fails.contains(serviceInstance)) {
                    lefts.add(serviceInstance);
                }
            }
            if (lefts.size() == 1) {
                return lefts.get(0);
            }
            return select0(lefts);
        }
        // 全部失败或者都成功时
        return select0(serviceInstanceList);
    }

    private ServiceInstance select0(List<ServiceInstance> serviceInstanceList) {
        return serviceInstanceList.get(doSelectIndex(serviceInstanceList.size()));
    }

    protected abstract int doSelectIndex(int bound);

    @Override
    public String toString() {
        return "LoadBalancer{" +
                "name='" + name + '\'' +
                '}';
    }

    private static LoadBalancer defaultLoadBalancer = new RandomLoadBalancer();
    private static Map<String, LoadBalancer> loadBalancerMap = new HashMap<>();

    static {
        put(defaultLoadBalancer);
        put(new RandomLoadBalancer());
    }

    private static void put(LoadBalancer loadBalancer) {
        loadBalancerMap.put(loadBalancer.name, loadBalancer);
    }

    public static LoadBalancer getLoadBalancer(String name) {
        return loadBalancerMap.getOrDefault(name, defaultLoadBalancer);
    }

}
