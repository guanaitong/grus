/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;

import org.apache.http.conn.DnsResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 主要为了防止dns不稳定的情形
 * Created by August.Zhou on 2017/6/22 17:28.
 */
public class ApacheCacheDnsResolver implements DnsResolver {
    public static final ApacheCacheDnsResolver INSTANCE = new ApacheCacheDnsResolver();


    public ApacheCacheDnsResolver() {
    }


    @Override
    public InetAddress[] resolve(String host) throws UnknownHostException {
        return CacheDns.INSTANCE.resolve(host);
    }


}
