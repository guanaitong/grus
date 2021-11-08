/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import com.ciicgat.sdk.util.http.HttpClientHelper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;

/**
 * 短链接工具类
 *
 * @author wanchongyang
 * @date 2020/4/18 2:23 下午
 */
public class ShortUrlUtil {
    private static final String PRIVATE_TOKEN_HEADER_NAME = "Private-Token";
    private static final String SHORT_URL_DOMAIN = "https://g-url.cn";
    private static final String CREATE_URL = SHORT_URL_DOMAIN.concat("/api/create");
    private static final String REVERSE_URL = SHORT_URL_DOMAIN.concat("/api/reverse");
    private static final Map<String, String> HEADER_MAP = new HashMap<>(16);

    static {
        HEADER_MAP.put(PRIVATE_TOKEN_HEADER_NAME, GlobalGconfConfig.getConfig().getProperties("security.properties").getProperty("shorturl_token"));
    }

    private ShortUrlUtil() {
    }

    /**
     * 生成短链接
     *
     * @param url 原始url
     * @return 短链接地址
     */
    public static String create(String url) {
        if (url != null && !"".equals(url)) {
            Map<String, Object> params = new HashMap<>(16);
            params.put("url", url);
            String content = HttpClientHelper.postJson(CREATE_URL, JSON.toJSONString(params), HEADER_MAP);
            ApiResponse<String> apiResponse = JSON.parse(content, new TypeReference<>() { });
            if (apiResponse.getCode() == 0) {
                return apiResponse.getData();
            }

            throw new BusinessRuntimeException(apiResponse.getCode(), apiResponse.getMsg());
        }

        return "";
    }

    /**
     * 还原短链接
     *
     * @param shortUrl 短链接地址
     * @return 原始地址
     */
    public static String reverse(String shortUrl) {
        if (shortUrl != null && !"".equals(shortUrl)) {
            Map<String, Object> params = new HashMap<>(16);
            params.put("url", shortUrl);
            var content = HttpClientHelper.postJson(REVERSE_URL, JSON.toJSONString(params), HEADER_MAP);
            ApiResponse<String> apiResponse = JSON.parse(content, new TypeReference<>() { });
            if (apiResponse.getCode() == 0) {
                return apiResponse.getData();
            }

            throw new BusinessRuntimeException(apiResponse.getCode(), apiResponse.getMsg());
        }

        return "";
    }
}
