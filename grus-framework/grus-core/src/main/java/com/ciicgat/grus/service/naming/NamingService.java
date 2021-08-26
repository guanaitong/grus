/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.naming;

import com.ciicgat.sdk.util.system.Systems;

/**
 * 服务名寻址
 * Created by August.Zhou on 2018-11-21 14:46.
 */
public interface NamingService {

    String HTTP_PROTOCOL_PREFIX = "http://";

    /**
     * 通过应用名称解析出应用域名
     * @param serviceName-> userdoor
     * @return http://userdoor或者http://userdoor.services.dev.ofc
     */
    String resolve(String serviceName);

    /**
     * 通过应用域名反解析出应用名
     * @param domain -> userdoor.services.dev.ofc
     * @return userdoor或者userdoor.services.dev.ofc
     */
    default String serviceLocation(String domain) {
        if (Systems.IN_K8S) {
            return null;
        }

        return domain;
    }

    ListNamingService DEFAULT = new ListNamingService(
            new LocalNamingService(),
            new K8sNamingService(),
            new DomainRuleNamingService());

}
