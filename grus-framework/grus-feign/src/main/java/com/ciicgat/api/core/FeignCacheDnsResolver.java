/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.sdk.util.system.WorkRegion;
import okhttp3.Dns;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author August
 * @date 3/11/2024 12:40 PM
 */
public class FeignCacheDnsResolver implements Dns {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeignCacheDnsResolver.class);
    public static final FeignCacheDnsResolver INSTANCE = new FeignCacheDnsResolver();
    private ConcurrentMap<String, List<InetAddress>> dnsMap = new ConcurrentHashMap<>();


    private FeignCacheDnsResolver() {
    }

    @NotNull
    @Override
    public List<InetAddress> lookup(@NotNull String host) throws UnknownHostException {
        try {
            InetAddress[] value = getAllByName(host);
            if (value != null && value.length > 0) {
                var v = Arrays.asList(value);
                dnsMap.put(host, v);
                return v;
            } else {
                return dnsMap.get(host);
            }
        } catch (UnknownHostException e) {
            LOGGER.warn("dns不稳定,host :" + host + ",尝试走缓存");
            List<InetAddress> value = dnsMap.get(host);
            if (value != null) {
                LOGGER.info("dns不稳定,host :" + host + ",走缓存成功");
                return value;
            }
            LOGGER.error("dns不稳定,host :" + host + ",走缓存失败", e);
            throw e;
        }
    }

    private static InetAddress[] getAllByName(String host) throws UnknownHostException {
        try {
            return InetAddress.getAllByName(host);
        } catch (UnknownHostException e) {
            WorkRegion currentWorkRegion = WorkRegion.getCurrentWorkRegion();
            if (currentWorkRegion == WorkRegion.STAGE_LYRA && host.endsWith(currentWorkRegion.getServiceDomainSuffix())) {
                return InetAddress.getAllByName("10.129.1.204");
            } else if (currentWorkRegion == WorkRegion.PROD_LYRA && host.endsWith(currentWorkRegion.getServiceDomainSuffix())) {
                return InetAddress.getAllByName("10.129.4.204");
            }
            throw e;
        }
    }
}
