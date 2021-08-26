/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.servlet.fallback;

import com.ciicgat.grus.fallback.FallbackConfig;
import com.ciicgat.grus.fallback.FallbackConstant;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.ConfigCollection;
import com.ciicgat.sdk.servlet.UriUtils;

import java.util.List;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/3/27 16:04
 * @Description:
 */
public class FallbackHelper {

    public static boolean isUriNeedFallback(String uri, ConfigCollection configCollection) {
        if (configCollection == null) {
            return false;
        }

        FallbackConfig fallbackConfig = configCollection.getBean(FallbackConstant.JSON_FILE_NAME, content -> {
            try {
                return JSON.parse(content, FallbackConfig.class);
            } catch (Exception e) {
                return null;
            }
        });

        // 判断开关是否要降级
        if (fallbackConfig == null
                || fallbackConfig.getProviderFallback() == null
                || !fallbackConfig.getProviderFallback().isFallback()) {
            return false;
        }

        // 判断路径是否降级
        if (UriUtils.isExclude(uri)) {
            return false;
        }

        List<String> excludes = fallbackConfig.getProviderFallback().getExcludePathList();
        List<String> includes = fallbackConfig.getProviderFallback().getIncludePathList();
        if (excludes != null) {
            for (String exclude : excludes) {
                if (safeMatch(uri, exclude)) {
                    return false;
                }
            }
        }


        boolean matchInclude = true;
        if (includes != null) {
            matchInclude = false;
            for (String include : includes) {
                matchInclude = matchInclude || safeMatch(uri, include);
            }
        }

        return matchInclude;
    }

    private static boolean safeMatch(String text, String regex) {
        try {
            return text.matches(regex);
        } catch (Exception e) {
            return false;
        }
    }
}
