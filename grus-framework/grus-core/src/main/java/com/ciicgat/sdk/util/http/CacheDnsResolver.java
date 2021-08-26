/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;

import okhttp3.Dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * 主要为了防止dns不稳定的情形
 * Created by August.Zhou on 2017/6/22 17:28.
 */
public class CacheDnsResolver implements Dns {
    public static final CacheDnsResolver INSTANCE = new CacheDnsResolver();


    public CacheDnsResolver() {
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        return CacheDns.INSTANCE.lookup(hostname);
    }

}
