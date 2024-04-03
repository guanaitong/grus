/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.naming;

import com.ciicgat.sdk.util.system.WorkRegion;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by August.Zhou on 2018-11-21 14:58.
 */
public class DomainRuleNamingService implements NamingService {

    @Override
    public String resolve(String serviceName, boolean secure) {
        WorkRegion workRegion = WorkRegion.getCurrentWorkRegion();

        if (workRegion == WorkRegion.STAGE_LYRA || workRegion == WorkRegion.PROD_LYRA) {
            //如果是lyra阿里云的，那么优先探测lyra阿里云本地的服务是否通，不通时，采用上海IDC的服务
            String serviceDomain = serviceName + "." + workRegion.getServiceDomainSuffix();
            if (isValid(serviceDomain)) {
                return HTTP_PROTOCOL_PREFIX + serviceDomain;
            } else {
                return HTTP_PROTOCOL_PREFIX + serviceName + "." + WorkRegion.PROD_SH.getServiceDomainSuffix();
            }
        }
        return HTTP_PROTOCOL_PREFIX + serviceName + "." + workRegion.getServiceDomainSuffix();

    }

    private static boolean isValid(String domain) {
        try {
            InetAddress[] value = InetAddress.getAllByName(domain);
            return value != null && value.length != 0;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
