/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Created by August.Zhou on 2019-02-20 10:52.
 */
public class HttpClientHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHelper.class);

    /**
     * HttpClient
     */

    public static final MediaType APPLICATION_JSON = MediaType.get("application/json;charset=utf-8");

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
        HttpUrl httpUrl = getHttpUrl(url, queryParameters);
        final Request.Builder requestBuilder = new Request.Builder().get().url(httpUrl);

        return executeRequest(requestBuilder, headers, httpTimeout);
    }

    private static HttpUrl getHttpUrl(String url, Map<String, ? extends Object> queryParameters) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (queryParameters != null && queryParameters.size() > 0) {
            final HttpUrl.Builder httpUrlBuilder = httpUrl.newBuilder();
            queryParameters.forEach((key, value) -> {
                if (key != null && value != null) {
                    httpUrlBuilder.addQueryParameter(key, String.valueOf(value));
                }
            });
            return httpUrlBuilder.build();
        }
        return httpUrl;
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
        FormBody.Builder formBody = new FormBody.Builder(StandardCharsets.UTF_8);

        if (formParameters != null && formParameters.size() > 0) {
            formParameters.forEach((key, value) -> {
                if (Objects.isNull(key) || Objects.isNull(value)) {
                    return;
                }
                formBody.add(key, value.toString());
            });

        }
        return post(url, formBody.build(), Collections.emptyMap(), headers, httpTimeout);
    }

    /**
     * POST application/json请求
     *
     * @param url      请求地址
     * @param jsonBody 转化为字符串的json体
     * @return
     */
    public static String postJson(String url, String jsonBody) {
        return postJson(url, jsonBody, Collections.emptyMap(), Collections.emptyMap(), null);
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
        return postJson(url, jsonBody, Collections.emptyMap(), headers, null);
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
        return postJson(url, jsonBody, Collections.emptyMap(), headers, httpTimeout);
    }

    /**
     * POST application/json请求
     *
     * @param url             请求地址
     * @param jsonBody        转化为字符串的json体
     * @param queryParameters url参数
     * @param headers         请求头部
     * @param httpTimeout     超时定义
     * @return
     */

    public static String postJson(String url, String jsonBody, Map<String, ? extends Object> queryParameters, Map<String, String> headers, HttpTimeout httpTimeout) {
        Objects.requireNonNull(jsonBody, "jsonBody should not be null");
        RequestBody requestBody = RequestBody.create(APPLICATION_JSON, jsonBody);
        return post(url, requestBody, queryParameters, headers, httpTimeout);
    }

    /**
     * 通用post请求
     *
     * @param url         请求地址
     * @param requestBody 请求body
     * @param headers     请求头部
     * @param httpTimeout 超时定义
     * @return
     */
    public static String post(String url, RequestBody requestBody, Map<String, ? extends Object> queryParameters, Map<String, String> headers, HttpTimeout httpTimeout) {
        HttpUrl httpUrl = getHttpUrl(url, queryParameters);
        final Request.Builder requestBuilder = new Request.Builder().post(requestBody).url(httpUrl);
        return executeRequest(requestBuilder, headers, httpTimeout);
    }

    private static String executeRequest(Request.Builder requestBuilder, Map<String, String> headers, HttpTimeout httpTimeout) {
        if (headers != null && headers.size() > 0) {
            headers.forEach((key, value) -> {
                if (key != null && value != null) {
                    requestBuilder.addHeader(key, value);
                }
            });
        }
        Request request = requestBuilder.build();
        return request(request, httpTimeout);
    }

    /**
     * 通用的http请求
     *
     * @param request http请求结构体
     * @return
     */
    public static String request(Request request) {
        return request(request, (HttpTimeout) null);
    }

    /**
     * 通用的http请求
     *
     * @param request     http请求结构体
     * @param httpTimeout 超时定义
     * @return
     */
    public static String request(Request request, HttpTimeout httpTimeout) {
        OkHttpClient okHttpClient = HttpClientSingleton.getOkHttpClient();
        if (httpTimeout != null) {
            okHttpClient = okHttpClient
                    .newBuilder()
                    .writeTimeout(httpTimeout.writeTimeout)
                    .readTimeout(httpTimeout.readTimeout)
                    .connectTimeout(httpTimeout.connectTimeout)
                    .build();
        }
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                return responseBody.string();
            } else {
                LOGGER.error("request {} ,response {} ,body {}", request.toString(), response.toString(), response.body() == null ? "" : response.body().string());
                return "";
            }
        } catch (Exception e) {
            throw new RuntimeException(request.toString(), e);
        }
    }


    /**
     * 通过的http请求，会通过{@link ResponseHandler}回调将response转化为对象
     *
     * @param request
     * @param responseHandler
     * @param <T>
     * @return
     */
    public static <T> T request(Request request,
                                ResponseHandler<? extends T> responseHandler) {
        return request(request, null, responseHandler);
    }

    public static <T> T request(Request request, HttpTimeout httpTimeout,
                                ResponseHandler<? extends T> responseHandler) {
        OkHttpClient okHttpClient = HttpClientSingleton.getOkHttpClient();
        if (httpTimeout != null) {
            okHttpClient = okHttpClient
                    .newBuilder()
                    .writeTimeout(httpTimeout.writeTimeout)
                    .readTimeout(httpTimeout.readTimeout)
                    .connectTimeout(httpTimeout.connectTimeout)
                    .build();
        }
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            return responseHandler.apply(response);
        } catch (Exception e) {
            throw new RuntimeException(request.toString(), e);
        }
    }


}
