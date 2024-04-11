/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;

import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.util.system.WorkRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by August.Zhou on 2019-07-19 13:31.
 */
class CacheDns {
    static final CacheDns INSTANCE = new CacheDns();
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheDns.class);
    private ConcurrentMap<String, InetAddress[]> dnsMap = new ConcurrentHashMap<>();
    private final boolean isLyra;

    private CacheDns() {
        isLyra = WorkRegion.getCurrentWorkRegion() == WorkRegion.STAGE_LYRA || WorkRegion.getCurrentWorkRegion() == WorkRegion.PROD_LYRA;
    }


    public InetAddress[] resolve(String host) throws UnknownHostException {
        try {
            InetAddress[] value = getAllByName(host);
            if (value != null && value.length > 0) {
                dnsMap.put(host, value);
            } else {
                value = dnsMap.get(host);
            }
            return value;
        } catch (UnknownHostException e) {
            LOGGER.warn("dns不稳定,host :{},尝试走缓存", host);
            InetAddress[] value = dnsMap.get(host);
            if (value != null) {
                LOGGER.info("dns不稳定,host :{},走缓存成功", host);
                return value;
            }
            LOGGER.error("dns不稳定,host :{},走缓存失败", host, e);
            throw e;
        }
    }

    private InetAddress[] getAllByName(String host) throws UnknownHostException {
        LOGGER.info("isLyra {},host {},currentRegion {}", isLyra, host, WorkRegion.getCurrentWorkRegion());
        if (isLyra && host.endsWith(WorkRegion.PROD_SH.getServiceDomainSuffix())) {
            Properties properties = GlobalGconfConfig.getConfig().getProperties("address.properties");
            String key;
            if (WorkRegion.getCurrentWorkRegion() == WorkRegion.STAGE_LYRA) {
                key = "shanghai_internal_gateway_ip_stage";
            } else {
                key = "shanghai_internal_gateway_ip_prod";
            }
            return InetAddress.getAllByName(properties.getProperty(key));
        }
        return InetAddress.getAllByName(host);
    }

    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        return Arrays.asList(resolve(hostname));
    }
}
