/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Albert on 2019-05-08.
 */
public abstract class GroupBot {
    public static String GLOBAL_GROUP_KEY = null;

    private GroupBot() {

    }

    public static void sendTextMsg(String groupBotKey, String content) {
        sendTextMsg(groupBotKey, content, null, null);
    }

    public static void sendTextMsg(String groupBotKey, String content, List<String> mentionedList, List<String> mentionedMobileList) {
        Map<String, Object> textMap = new HashMap<>(2);
        textMap.put("content", content);
        if (Objects.nonNull(mentionedList) && !mentionedList.isEmpty()) {
            textMap.put("mentioned_list", mentionedList);
        }
        if (Objects.nonNull(mentionedMobileList) && !mentionedMobileList.isEmpty()) {
            textMap.put("mentioned_mobile_list", mentionedMobileList);
        }
        Map<String, Object> msg = Map.of("msgtype", "text", "text", textMap);
        MsgClient.sendGroupBotMsg(groupBotKey, msg);
    }

    public static void sendMarkdownMsg(String groupBotKey, String content) {
        Map<String, Object> msg = Map.of("msgtype", "markdown", "markdown", Map.of("content", content));
        MsgClient.sendGroupBotMsg(groupBotKey, msg);
    }

    public static void sendImageMsg(String groupBotKey, byte[] imageData) {
        Map<String, Object> msg = Map.of("msgtype", "image",
                "image", Map.of("base64", Base64.getEncoder().encodeToString(imageData), "md5", DigestUtils.md5Hex(imageData)));
        MsgClient.sendGroupBotMsg(groupBotKey, msg);
    }

    public static void sendNewsMsg(String groupBotKey, List<NewsArticle> newsArticles) {
        Map<String, Object> msg = Map.of("msgtype", "news",
                "news", Map.of("articles", newsArticles));
        MsgClient.sendGroupBotMsg(groupBotKey, msg);
    }

    public static final class NewsArticle {
        private String title;
        private String description;
        private String url;
        @JsonProperty("picurl")
        private String picUrl;

        public String getTitle() {
            return title;
        }

        public NewsArticle setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public NewsArticle setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public NewsArticle setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public NewsArticle setPicUrl(String picUrl) {
            this.picUrl = picUrl;
            return this;
        }
    }


}
