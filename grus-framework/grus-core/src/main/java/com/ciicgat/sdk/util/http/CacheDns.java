/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by August.Zhou on 2019-07-19 13:31.
 */
class CacheDns {
    static final CacheDns INSTANCE = new CacheDns();
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheDns.class);
    private ConcurrentMap<String, InetAddress[]> dnsMap = new ConcurrentHashMap<>();


    private CacheDns() {
    }


    public InetAddress[] resolve(String host) throws UnknownHostException {
        try {
            InetAddress[] value = InetAddress.getAllByName(host);
            if (value != null && value.length > 0) {
                dnsMap.put(host, value);
            } else {
                value = dnsMap.get(host);
            }
            return value;
        } catch (UnknownHostException e) {
            LOGGER.warn("dns不稳定,host :" + host + ",尝试走缓存");
            InetAddress[] value = dnsMap.get(host);
            if (value != null) {
                LOGGER.info("dns不稳定,host :" + host + ",走缓存成功");
                return value;
            }
            LOGGER.error("dns不稳定,host :" + host + ",走缓存失败", e);
            throw e;
        }
    }

    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        return Arrays.asList(resolve(hostname));
    }
}
