/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util;

import com.ciicgat.grus.service.naming.K8sNamingService;
import com.ciicgat.grus.service.naming.NamingService;
import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkIdc;
import com.ciicgat.sdk.util.system.WorkRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * k8s环境下对ws地址的特殊处理
 * http://userdoor.services.dev.ofc/person/get => http://userdoor/person/get
 *
 * @author wanchongyang
 * @date 2020/3/15 10:59 下午
 */
public class K8sUrlUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(K8sUrlUtil.class);

    /**
     * service域名后缀：.services.product.sh
     */
    private static final String SERVICE_NAME_SUFFIX;
    /**
     * 域名正则表达式模板，其中%s替换为SERVICE_NAME_SUFFIX
     */
    private static final String DOMAIN_REGEX_FORMAT = "(\\w|-)+(%s)";
    /**
     * 完整的域名正则表达式：(\w|-)+(.services.product.sh)
     */
    private static final String DOMAIN_REGEX;
    private static final NamingService NAMING_SERVICE;
    private static final Pattern DOMAIN_PATTERN;
    private static ConcurrentHashMap<String, String> URL_CACHE = new ConcurrentHashMap<>(128);

    private K8sUrlUtil() {

    }

    static {
        SERVICE_NAME_SUFFIX = ".".concat(WorkRegion.getCurrentWorkRegion().getServiceDomainSuffix());
        DOMAIN_REGEX = String.format(DOMAIN_REGEX_FORMAT, SERVICE_NAME_SUFFIX);
        DOMAIN_PATTERN = Pattern.compile(DOMAIN_REGEX);
        NAMING_SERVICE = new K8sNamingService();
    }

    public static String convert(String url) {
        Objects.requireNonNull(url);
        if (!Systems.IN_K8S) {
            return url;
        }

        WorkRegion workRegion = WorkRegion.getCurrentWorkRegion();
        // 阿里云环境直接返回
        if (workRegion.isProduct() && workRegion.getWorkIdc() == WorkIdc.ALI) {
            return url;
        }

        // from cache
        if (URL_CACHE.contains(url)) {
            return URL_CACHE.get(url);
        }

        String newUrl = convert0(url);
        String result = URL_CACHE.putIfAbsent(url, newUrl);
        if (result == null) {
            LOGGER.info("{} => {}", url, newUrl);
            return newUrl;
        }

        return result;
    }

    private static String convert0(String url) {
        Matcher matcher = DOMAIN_PATTERN.matcher(url);
        if (matcher.find()) {
            String domain = matcher.group();

            String appName = NAMING_SERVICE.serviceLocation(domain);
            return url.replace(domain, appName);
        }

        return url;
    }
}
