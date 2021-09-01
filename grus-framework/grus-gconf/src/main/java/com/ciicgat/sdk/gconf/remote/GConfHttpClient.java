/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.gconf.ConfigApp;
import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.util.http.HttpClientSingleton;
import com.ciicgat.sdk.util.system.Systems;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * https://guide.wuxingdev.cn/middleware/gconf.html
 * <p>
 * Created by August.Zhou on 2016/10/17 15:18.
 */
public class GConfHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GConfHttpClient.class);
    private final String baseUrl;
    private final OkHttpClient okHttpClient;

    public GConfHttpClient(String domain) {
        this.baseUrl = "http://" + domain + "/api";
        OkHttpClient.Builder builder = HttpClientSingleton.getOkHttpClient().newBuilder();
        builder.interceptors().clear();
        this.okHttpClient = builder.build();
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
        return JSON.parse(content, ConfigApp.class);
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
        return JSON.parse(content, new TypeReference<>() {
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
        LinkedHashMap<String, String> res = JSON.parse(content, new TypeReference<>() {
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
        params.put("clientId", Systems.CLIENT_ID);
        params.put("configAppIdList", String.join(",", configAppIds));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("client {} start watch appIdList {}", Systems.CLIENT_ID, String.join(",", configAppIds));
        }
        String content = getContent("/watch", params);
        if (content.isEmpty()) {
            return Collections.emptyList();
        }
        return JSON.parse(content, new TypeReference<>() {
        });
    }


    private String getContent(String path, Map<String, String> params) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        String requestUri = baseUrl + path;
        final HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(requestUri).newBuilder();
        params.forEach((key, value) -> {
            if (key != null && value != null) {
                httpUrlBuilder.addQueryParameter(key, value);
            }
        });
        HttpUrl httpUrl = httpUrlBuilder.build();
        int count = 4;
        Exception exception = null;
        while (count-- > 0) {
            final Request request = new Request.Builder().get().url(httpUrl).build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    LOGGER.error("request {} ,response {} ,body {}", request, response, response.body() == null ? "" : response.body().string());
                    return "";
                }
            } catch (Exception e) {
                exception = e;
            }
            Threads.sleep(500);
        }
        LOGGER.error("error happen", exception);
        return "";
    }

}
