/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;

import com.ciicgat.sdk.gconf.ConfigApp;
import com.ciicgat.sdk.gconf.GconfConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * https://guide.wuxingdev.cn/middleware/gconf.html
 * <p>
 * Created by August.Zhou on 2016/10/17 15:18.
 */
public class GConfHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GConfHttpClient.class);
    private final GconfConfig gconfConfig;
    private final HttpClient httpClient;
    private final String clientId;

    public GConfHttpClient(GconfConfig gconfConfig) {
        this.gconfConfig = gconfConfig;
        this.clientId = UUID.randomUUID().toString();
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    }

    /**
     * 获取configApp信息
     *
     * @param configAppId
     * @return
     */
    public ConfigApp getConfigApp(String configAppId) {
        Map<String, String> params = new HashMap<>(1);
        params.put("configAppId", configAppId);
        String content = getContent("/getConfigApp", params);
        if (content == null || content.isEmpty()) {
            return null;
        }
        return InnerJson.parse(content, ConfigApp.class);
    }

    /**
     * 获取配置集合Key列表
     *
     * @param configAppId
     * @return
     */
    public List<String> listConfigKeys(String configAppId) {
        Map<String, String> params = new HashMap<>(1);
        params.put("configAppId", configAppId);
        String content = getContent("/listConfigKeys", params);
        return InnerJson.parse(content, new TypeReference<>() {
        });
    }

    /**
     * 获取单个配置项值
     *
     * @param configAppId
     * @param key
     * @return
     */
    public String getConfig(String configAppId, String key) {
        Map<String, String> params = new HashMap<>(2);
        params.put("configAppId", configAppId);
        params.put("key", key);
        return getContent("/getConfig", params);
    }

    /**
     * @param configAppId
     * @return
     */
    public Map<String, String> listConfigs(String configAppId) {
        Map<String, String> params = new HashMap<>(2);
        params.put("configAppId", configAppId);
        String content = getContent("/listConfigs", params);
        LinkedHashMap<String, String> res = InnerJson.parse(content, new TypeReference<>() {
        });
        for (Map.Entry<String, String> entry : res.entrySet()) {
            res.put(entry.getKey(), entry.getValue());
        }
        return Collections.unmodifiableMap(res);
    }


    /**
     * 返回需要更新的appId列表
     *
     * @param configAppIds
     * @return
     */
    public List<String> watch(Collection<String> configAppIds) {
        Map<String, String> params = new HashMap<>(2);
        params.put("clientId", clientId);
        params.put("configAppIdList", String.join(",", configAppIds));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("client {} start watch appIdList {}", clientId, String.join(",", configAppIds));
        }
        String content = getContent("/watch", params);
        if (content.isEmpty()) {
            return Collections.emptyList();
        }
        return InnerJson.parse(content, new TypeReference<>() {
        });
    }


    private String getContent(String path, Map<String, String> params) {
        StringBuilder requestUri = new StringBuilder("http://" + gconfConfig.getDomain() + "/api").append(path);
        if (params != null && !params.isEmpty()) {
            requestUri.append("?");
            params.forEach((key, value) -> {
                if (key != null && value != null) {
                    requestUri.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
            });
        }
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(requestUri.toString())).build();
        int count = 4;
        Exception exception = null;
        while (count-- > 0) {
            try {
                HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (httpResponse.statusCode() == 200) {
                    return httpResponse.body();
                } else {
                    LOGGER.error("request {} ,code {} ,body {}", requestUri, httpResponse.statusCode(), httpResponse.body() == null ? "" : httpResponse.body());
                    return "";
                }
            } catch (Exception e) {
                exception = e;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
            }
        }
        LOGGER.error("error happen", exception);
        return "";
    }

}
