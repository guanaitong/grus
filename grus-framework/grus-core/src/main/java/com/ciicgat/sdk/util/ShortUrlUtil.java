/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.convert.ApiResponse;
import com.ciicgat.sdk.lang.exception.BusinessRuntimeException;
import com.ciicgat.sdk.util.http.HttpClientHelper;
import com.ciicgat.sdk.util.http.HttpTimeout;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;
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
    private static final String PRIVATE_TOKEN_HEADER_VALUE = "wjdxavE4UUsJqGBtGxFT";
    private static final String SHORT_URL_DOMAIN = "https://g-url.cn";
    private static final String CREATE_URL = SHORT_URL_DOMAIN.concat("/api/create");
    private static final String REVERSE_URL = SHORT_URL_DOMAIN.concat("/api/reverse");
    private static final Map<String, String> HEADER_MAP = new HashMap<>(16);

    static {
        HEADER_MAP.put(PRIVATE_TOKEN_HEADER_NAME, PRIVATE_TOKEN_HEADER_VALUE);
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
        return create(url, null);
    }

    /**
     * 生成短链接
     *
     * @param url         长连接
     * @param httpTimeout 超时时间
     * @return 短链接地址
     */
    public static String create(String url, HttpTimeout httpTimeout) {
        if (url != null && !"".equals(url)) {
            Map<String, Object> params = new HashMap<>(16);
            params.put("url", url);
            String content = HttpClientHelper.postJson(CREATE_URL, JSON.toJSONString(params), HEADER_MAP, httpTimeout);
            ApiResponse<String> apiResponse = JSON.parse(content, new TypeReference<>() {
            });
            if (apiResponse.getCode() == 0) {
                return apiResponse.getData();
            }

            throw new BusinessRuntimeException(apiResponse.getCode(), apiResponse.getMsg());
        }

        return "";
    }

    /**
     * 生成短链接
     *
     * @param url                   原始url
     * @param connectTimeoutSeconds 连接超时时间
     * @param readTimeoutSeconds    读取超时时间
     * @param writeTimeoutSeconds   写入超时时间
     * @return 短链接地址
     */
    public static String create(String url, long connectTimeoutSeconds, long readTimeoutSeconds, long writeTimeoutSeconds) {
        long connectTimeout = connectTimeoutSeconds < 10 ? 10 : connectTimeoutSeconds;
        long readTimeout = 180 < readTimeoutSeconds ? 180 : readTimeoutSeconds;
        long writeTimeout = 60 < writeTimeoutSeconds ? 60 : writeTimeoutSeconds;

        HttpTimeout httpTimeout = new HttpTimeout()
                .connectTimeout(Duration.ofSeconds(connectTimeout))
                .readTimeout(Duration.ofSeconds(readTimeout))
                .writeTimeout(Duration.ofSeconds(writeTimeout));
        return create(url, httpTimeout);
    }

    /**
     * 还原短链接
     *
     * @param shortUrl 短链接地址
     * @return 原始地址
     */
    public static String reverse(String shortUrl) {
        return reverse(shortUrl, null);
    }

    /**
     * 还原短链接
     * @param shortUrl 短链接地址
     * @param httpTimeout 超时时间
     * @return 原始地址
     */
    public static String reverse(String shortUrl, HttpTimeout httpTimeout) {
        if (shortUrl != null && !"".equals(shortUrl)) {
            Map<String, Object> params = new HashMap<>(16);
            params.put("url", shortUrl);
            var content = HttpClientHelper.postJson(REVERSE_URL, JSON.toJSONString(params), HEADER_MAP, httpTimeout);
            ApiResponse<String> apiResponse = JSON.parse(content, new TypeReference<>() {
            });
            if (apiResponse.getCode() == 0) {
                return apiResponse.getData();
            }

            throw new BusinessRuntimeException(apiResponse.getCode(), apiResponse.getMsg());
        }
        return "";
    }


    /**
     * 还原短链接
     * @param shortUrl 短链接地址
     * @param connectTimeoutSeconds 连接超时时间
     * @param readTimeoutSeconds   读取超时时间
     * @param writeTimeoutSeconds 写入超时时间
     * @return 原始地址
     */
    public static String reverse(String shortUrl, long connectTimeoutSeconds, long readTimeoutSeconds, long writeTimeoutSeconds) {
        long connectTimeout = connectTimeoutSeconds < 10 ? 10 : connectTimeoutSeconds;
        long readTimeout = 180 < readTimeoutSeconds ? 180 : readTimeoutSeconds;
        long writeTimeout = 60 < writeTimeoutSeconds ? 60 : writeTimeoutSeconds;

        HttpTimeout httpTimeout = new HttpTimeout()
                .connectTimeout(Duration.ofSeconds(connectTimeout))
                .readTimeout(Duration.ofSeconds(readTimeout))
                .writeTimeout(Duration.ofSeconds(writeTimeout));
        return reverse(shortUrl, httpTimeout);
    }
}
