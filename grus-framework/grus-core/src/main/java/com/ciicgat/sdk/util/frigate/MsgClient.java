/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import com.ciicgat.grus.json.JSON;
import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.util.http.HttpClientSingleton;
import com.ciicgat.sdk.util.system.WorkRegion;
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
abstract class MsgClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgClient.class);
    /**
     * 单线程，中等优先级，队列最大500个(超过任务丢弃)
     * 将消息发送对业务的影响，尽可能降低到最低
     */
    static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(4096), Threads.newDaemonThreadFactory("frigate_notifier", Thread.MIN_PRIORITY), Threads.LOGGER_REJECTEDEXECUTIONHANDLER);

    final static OkHttpClient OK_HTTP_CLIENT;

    static {
        OkHttpClient.Builder builder = HttpClientSingleton.getOkHttpClient().newBuilder().readTimeout(2, TimeUnit.SECONDS);
        builder.interceptors().clear();
        OK_HTTP_CLIENT = builder.build();
    }

    /**
     * DEV/TEST 环境消息不发送
     */
    private static boolean skip = WorkRegion.getCurrentWorkRegion().isDevelopOrTest();


    private MsgClient() {
    }

    public static void setSkip(boolean skip) {
        MsgClient.skip = skip;
    }

    /**
     * Frigate 对外消息接口。
     *
     * @param path
     * @param params
     */
    static void send(String path, Map<String, String> params, FrigateMessage frigateMessage) {
        if (skip) return;
        String baseUrl = GlobalGconfConfig.getConfig().getProperties("frigate.properties").getProperty("frigate.message.base.url");
        String url = baseUrl + path;
        HttpUrl httpUrl = HttpUrl.parse(url);
        final HttpUrl.Builder httpUrlBuilder = httpUrl.newBuilder();
        params.forEach((key, value) -> {
            if (key != null && value != null) {
                httpUrlBuilder.addQueryParameter(key, value);
            }
        });
        executeRequest(httpUrlBuilder.toString(), frigateMessage);
    }

    static void sendGroupBotMsg(String groupBotKey, Object body) {
        if (skip) return;
        executeRequest(String.format("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=%s", groupBotKey), body);
    }


    private static void executeRequest(String url, Object jsonBody) {
        try {
            EXECUTOR_SERVICE.execute(() -> {
                RequestBody requestBody = RequestBody.create(com.ciicgat.sdk.util.http.HttpClientHelper.APPLICATION_JSON, JSON.toJSONString(jsonBody));
                Request request = new Request.Builder().post(requestBody).url(url).build();
                try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        LOGGER.error("request {} ,response {} ,body {}", request, response, response.body() == null ? "" : response.body().string());
                    }
                } catch (Exception e) {
                    LOGGER.error("send frigate msg error", e);
                }
            });
        } catch (Exception e) {
            LOGGER.error("发送消息异常", e);
        }
    }

}
