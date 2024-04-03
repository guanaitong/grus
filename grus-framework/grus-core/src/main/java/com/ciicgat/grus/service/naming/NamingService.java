/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.naming;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.sdk.util.system.Systems;

/**
 * 服务名寻址
 * Created by August.Zhou on 2018-11-21 14:46.
 */
public interface NamingService {

    String HTTP_PROTOCOL_PREFIX = "http://";

    String HTTPS_PROTOCOL_PREFIX = "https://";

    /**
     * 通过应用名称解析出应用域名
     *
     * @param serviceName-> userdoor
     * @return http://userdoor或者http://userdoor.services.dev.ofc
     */
    default String resolve(String serviceName) {
        return resolve(serviceName, false);
    }

    /**
     * 如果secure为true，则返回https协议的域名
     * @param serviceName
     * @param secure
     * @return
     */
    String resolve(String serviceName, boolean secure);

    default String resolveBaseUrl(ServiceName serviceName) {
        String hostUrl = resolve(serviceName.value());
        String urlPathPrefix = serviceName.urlPathPrefix();
        if (!urlPathPrefix.isEmpty()) {
            if (hostUrl.endsWith("/") || urlPathPrefix.startsWith("/")) {
                return hostUrl + urlPathPrefix;
            } else {
                return hostUrl + "/" + urlPathPrefix;
            }
        }
        return hostUrl;
    }

    /**
     * 通过应用域名反解析出应用名
     *
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
