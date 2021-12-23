/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;

import com.ciicgat.grus.core.Module;
import com.ciicgat.grus.performance.SlowLogger;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Created by August.Zhou on 2019-02-20 10:52.
 */
public class ApacheHttpClientHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApacheHttpClientHelper.class);

    /**
     * HttpClient
     */


    /**
     * GET请求
     *
     * @param url 请求地址
     * @return
     */
    public static String get(String url) {
        return get(url, Collections.emptyMap(), Collections.emptyMap(), null);
    }

    /**
     * GET请求
     *
     * @param url             请求地址
     * @param queryParameters 请求参数
     * @return
     */
    public static String get(String url, Map<String, ? extends Object> queryParameters) {
        return get(url, queryParameters, Collections.emptyMap(), null);
    }

    /**
     * GET请求
     *
     * @param url             请求地址
     * @param queryParameters 请求参数
     * @param headers         请求头部
     * @return
     */
    public static String get(String url, Map<String, ? extends Object> queryParameters, Map<String, String> headers) {
        return get(url, queryParameters, headers, null);
    }

    /**
     * GET请求
     *
     * @param url             请求地址
     * @param queryParameters 请求参数
     * @param headers         请求头部
     * @param httpTimeout     超时定义
     * @return
     */
    public static String get(String url, Map<String, ? extends Object> queryParameters, Map<String, String> headers, HttpTimeout httpTimeout) {
        RequestBuilder requestBuilder = RequestBuilder.get(url).setCharset(StandardCharsets.UTF_8);

        if (queryParameters != null) {
            queryParameters.forEach((k, v) -> {
                if (k != null && v != null) {
                    requestBuilder.addParameter(k, v.toString());
                }
            });
        }


        return executeRequest(requestBuilder, headers, httpTimeout);
    }


    /**
     * POST表单请求
     *
     * @param url            请求地址
     * @param formParameters 表单参数
     * @return
     */
    public static String postForm(String url, Map<String, ? extends Object> formParameters) {
        return postForm(url, formParameters, Collections.emptyMap(), null);
    }

    /**
     * POST表单请求
     *
     * @param url            请求地址
     * @param formParameters 表单参数
     * @param headers        请求头部
     * @return
     */
    public static String postForm(String url, Map<String, ? extends Object> formParameters, Map<String, String> headers) {
        return postForm(url, formParameters, headers, null);
    }

    /**
     * POST表单请求
     *
     * @param url            请求地址
     * @param formParameters 表单参数
     * @param headers        请求头部
     * @param httpTimeout    超时定义
     * @return
     */
    public static String postForm(String url, Map<String, ? extends Object> formParameters, Map<String, String> headers, HttpTimeout httpTimeout) {
        RequestBuilder requestBuilder = RequestBuilder.post(url).setCharset(StandardCharsets.UTF_8);
        if (formParameters != null) {
            formParameters.forEach((k, v) -> {
                if (k != null && v != null) {
                    requestBuilder.addParameter(k, v.toString());
                }
            });
        }
        return executeRequest(requestBuilder, headers, httpTimeout);
    }

    /**
     * POST application/json请求
     *
     * @param url      请求地址
     * @param jsonBody 转化为字符串的json体
     * @return
     */
    public static String postJson(String url, String jsonBody) {
        return postJson(url, jsonBody, Collections.emptyMap(), null);
    }

    /**
     * POST application/json请求
     *
     * @param url      请求地址
     * @param jsonBody 转化为字符串的json体
     * @param headers  请求头部
     * @return
     */
    public static String postJson(String url, String jsonBody, Map<String, String> headers) {
        return postJson(url, jsonBody, headers, null);
    }


    /**
     * POST application/json请求
     *
     * @param url         请求地址
     * @param jsonBody    转化为字符串的json体
     * @param headers     请求头部
     * @param httpTimeout 超时定义
     * @return
     */

    public static String postJson(String url, String jsonBody, Map<String, String> headers, HttpTimeout httpTimeout) {
        Objects.requireNonNull(jsonBody, "jsonBody should not be null");
        RequestBuilder requestBuilder = RequestBuilder.post(url).setCharset(StandardCharsets.UTF_8);

        StringEntity stringEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
        requestBuilder.setEntity(stringEntity);
        return executeRequest(requestBuilder, headers, httpTimeout);
    }


    private static String executeRequest(RequestBuilder requestBuilder, Map<String, String> headers, HttpTimeout httpTimeout) {
        if (requestBuilder.getCharset() == null) {
            requestBuilder.setCharset(StandardCharsets.UTF_8);
        }
        if (headers != null) {
            headers.forEach((k, v) -> requestBuilder.addHeader(new BasicHeader(k, v)));
        }

        if (httpTimeout != null) {
            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setRedirectsEnabled(false)
                    .setSocketTimeout((int) httpTimeout.readTimeout.toMillis())
                    .setConnectTimeout((int) httpTimeout.connectTimeout.toMillis())
                    .build();
            requestBuilder.setConfig(requestConfig);
        }

        HttpUriRequest httpUriRequest = requestBuilder.build();
        return request(httpUriRequest);
    }


    /**
     * 通用的http请求
     *
     * @param httpUriRequest http请求结构体
     * @return
     */
    public static String request(HttpUriRequest httpUriRequest) {
        long start = System.nanoTime();
        CloseableHttpClient closeableHttpClient = HttpClientSingleton.getApacheHttpClient();
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpUriRequest)) {
            int statusCode = response.getStatusLine().getStatusCode(); //NOSONAR
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            if (statusCode >= 400) {
                LOGGER.error("{}|[{}] -> {}", statusCode, httpUriRequest.getURI().toString(), result);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SlowLogger.logEvent(Module.HTTP_CLIENT, System.nanoTime() - start, httpUriRequest.toString());
        }
    }


    /**
     * 通过的http请求，会通过{@link ResponseHandler}回调将response转化为对象
     *
     * @param httpUriRequest
     * @param responseHandler
     * @param <T>
     * @return
     */
    public static <T> T request(HttpUriRequest httpUriRequest,
                                org.apache.http.client.ResponseHandler<? extends T> responseHandler) {
        long start = System.currentTimeMillis();
        CloseableHttpClient closeableHttpClient = HttpClientSingleton.getApacheHttpClient();
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpUriRequest)) {
            long cost = System.currentTimeMillis() - start;
            if (cost > 500) { //超过500毫秒就定义为慢请求
                LOGGER.warn("slow request,cost {} millis,request {}", cost, httpUriRequest);
            }
            return responseHandler.handleResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
