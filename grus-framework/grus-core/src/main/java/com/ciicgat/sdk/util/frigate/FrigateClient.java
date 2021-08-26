/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.util.http.HttpClientSingleton;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Frigate 工具类共享
 * <p>
 * Created by Albert on 2018/10/29.
 */
public abstract class FrigateClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrigateClient.class);

    /**
     * 单线程，中等优先级，队列最大500个(超过任务丢弃)
     * 将消息发送对业务的影响，尽可能降低到最低
     */
    static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(4096), Threads.newDaemonThreadFactory("frigate_notifier", Thread.NORM_PRIORITY), Threads.LOGGER_REJECTEDEXECUTIONHANDLER);

    private final static OkHttpClient OK_HTTP_CLIENT;

    static {
        OkHttpClient.Builder builder = HttpClientSingleton.getOkHttpClient().newBuilder().readTimeout(2, TimeUnit.SECONDS);
        builder.interceptors().clear();
        OK_HTTP_CLIENT = builder.build();
    }

    /**
     * DEV 环境消息不发送
     */
    private static boolean skip = null == System.getenv("WORK_ENV") || "dev".equals(System.getenv("WORK_ENV"));


    private FrigateClient() {
    }

    public static void setSkip(boolean skip) {
        FrigateClient.skip = skip;
    }

    /**
     * Frigate 对外消息接口。
     *
     * @param url
     * @param params
     */
    static void send(String url, Map<String, String> params, FrigateMessage frigateMessage) {
        if (skip) return;

        try {
            //同样的消息限制频率每2分钟发送一次
            EXECUTOR_SERVICE.execute(() -> executeRequest(url, params, frigateMessage));
        } catch (Exception e) {
            LOGGER.error("发送消息到Frigate异常", e);
        }

    }


    private static void executeRequest(String url, Map<String, String> params, FrigateMessage frigateMessage) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        final HttpUrl.Builder httpUrlBuilder = httpUrl.newBuilder();
        params.forEach((key, value) -> {
            if (key != null && value != null) {
                httpUrlBuilder.addQueryParameter(key, value);
            }
        });
        RequestBody requestBody = RequestBody.create(com.ciicgat.sdk.util.http.HttpClientHelper.APPLICATION_JSON, frigateMessage.toString());
        Request request = new Request.Builder().post(requestBody).url(httpUrlBuilder.build()).build();
        Call call = OK_HTTP_CLIENT.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                LOGGER.error("request {} ,response {} ,body {}", request.toString(), response.toString(), response.body() == null ? "" : response.body().string());
            }
        } catch (Exception e) {
            LOGGER.error("send frigate msg error", e);
        }
    }

}
