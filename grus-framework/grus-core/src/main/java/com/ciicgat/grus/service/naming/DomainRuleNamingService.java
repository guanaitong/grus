/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.naming;

import com.ciicgat.sdk.util.system.WorkEnv;
import com.ciicgat.sdk.util.system.WorkIdc;
import com.ciicgat.sdk.util.system.WorkRegion;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by August.Zhou on 2018-11-21 14:58.
 */
public class DomainRuleNamingService implements NamingService {

    @Override
    public String resolve(String serviceName) {
        WorkRegion workRegion = WorkRegion.getCurrentWorkRegion();

        if (workRegion.isProduct() && workRegion.getWorkIdc() == WorkIdc.ALI) {
            //如果是阿里云的，那么优先探测阿里云本地的服务是否通，不通时，采用上海IDC的服务
            String aliServiceDomain = serviceName + "." + workRegion.getServiceDomainSuffix();
            try {
                InetAddress[] value = InetAddress.getAllByName(aliServiceDomain);
                if (value == null || value.length == 0) {
                    return HTTP_PROTOCOL_PREFIX + serviceName + ".services." + WorkEnv.PRODUCT.getInnerDomainSuffix() + "." + WorkIdc.SH.getInnerDomainSuffix();
                }
            } catch (UnknownHostException e) {
                return HTTP_PROTOCOL_PREFIX + serviceName + ".services." + WorkEnv.PRODUCT.getInnerDomainSuffix() + "." + WorkIdc.SH.getInnerDomainSuffix();
            }
            return HTTP_PROTOCOL_PREFIX + aliServiceDomain;
        }
        return HTTP_PROTOCOL_PREFIX + serviceName + "." + workRegion.getServiceDomainSuffix();

    }
}
