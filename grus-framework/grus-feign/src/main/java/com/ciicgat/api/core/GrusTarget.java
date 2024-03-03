/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.grus.service.Services;
import com.ciicgat.grus.service.naming.NamingService;
import feign.Request;
import feign.RequestTemplate;
import feign.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by August.Zhou on 2019-07-03 12:50.
 */
class GrusTarget<T> implements Target<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrusTarget.class);
    private final Class<T> type;
    private final String name;
    private final String url;


    GrusTarget(Class<T> serviceClazz, ServiceName serviceName, NamingService namingService) {
        this.type = serviceClazz;
        this.name = serviceName.value();
        this.url = namingService.resolveBaseUrl(serviceName, Services.isSecure(serviceClazz));
        LOGGER.info("create service instance of {},  serviceName {}, baseUrl {}", serviceClazz, serviceName, url);
    }

    private static String buildTargetUrl(String hostUrl, String urlPathPrefix) {
        if (!urlPathPrefix.isEmpty()) {
            if (hostUrl.endsWith("/") || urlPathPrefix.startsWith("/")) {
                return hostUrl + urlPathPrefix;
            } else {
                return hostUrl + "/" + urlPathPrefix;
            }
        }
        return hostUrl;
    }


    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String url() {
        return url;
    }


    /* no authentication or other special activity. just insert the url. */
    @Override
    public Request apply(RequestTemplate input) {
        input.target(url);
        return input.request();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GrusTarget) {
            GrusTarget<?> other = (GrusTarget) obj;
            return type.equals(other.type)
                    && name.equals(other.name)
                    && url.equals(other.url);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + type.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GrusTarget{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
