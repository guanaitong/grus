/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import com.ciicgat.sdk.lang.tool.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Created by Albert on 2019-05-08.
 */
public abstract class GroupBot {

    private static final String WEB_HOOK_PATTERN = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=%s";

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupBot.class);

    private static final HttpClient HTTP_CLIENT = HttpClient
            .newBuilder()
            .connectTimeout(Duration.ofMillis(5000))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public static void sendTextMsg(String groupBotKey, String content) {
        final String textMsg = new MessagePacket("text", new MessagePacket.Text(content)).toString();
        FrigateClient.EXECUTOR_SERVICE.execute(() ->
                executeRequest(String.format(WEB_HOOK_PATTERN, groupBotKey), textMsg)
        );
    }

    public static void sendMarkdownMsg(String groupBotKey, String content) {
        String markdownMsg = new MessagePacket("markdown", new MessagePacket.Markdown(content)).toString();
        FrigateClient.EXECUTOR_SERVICE.execute(() ->
                executeRequest(String.format(WEB_HOOK_PATTERN, groupBotKey), markdownMsg)
        );
    }

    private static void executeRequest(String url, String content) {

        try {
            HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(Bytes.toBytes(content));
            URI uri = new URI(url);
            final HttpRequest httpRequest = HttpRequest
                    .newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofMillis(30000))
                    .header("Content-Type", "application/json")
                    .POST(bodyPublisher)
                    .build();

            HttpResponse<String> httpResponse = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                LOGGER.info("frigate notify failed , code {} ,body {}", httpResponse.statusCode(), httpResponse.body());
            }
        } catch (Exception ex) {
            LOGGER.error("企业微信请求异常", ex);
        }

    }


}
