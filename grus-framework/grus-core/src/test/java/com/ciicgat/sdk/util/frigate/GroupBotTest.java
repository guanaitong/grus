/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Albert on 2019-05-08.
 */
public class GroupBotTest {

    /**
     * 企业微信机器人的 key，用来发送报警通知
     */
    private static final String GROUP_BOT_KEY = "ceed6aa3-ca81-4e9e-9638-9bc23e115a16";

    @BeforeClass
    public static void beforeClass() throws Exception {
        MsgClient.setSkip(false);
    }

    @After
    public void tearDown() throws Exception {
        try {
            if (Objects.isNull(System.getenv("CI"))) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void sendMarkdownMsg() {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# Grus 构建知会").append("\n");
        markdown.append(">").append("警告:").append("<font color=\"warning\">这是警告（黄色）</font>").append("\n");
        markdown.append(">").append("提示:").append("<font color=\"info\">这是提示（绿色）</font>").append("\n");
        markdown.append(">").append("注释:").append("<font color=\"comment\">这是备注（灰色）</font>").append("\n");
        markdown.append(">").append(new Date());
        GroupBot.sendMarkdownMsg(GROUP_BOT_KEY, markdown.toString());
    }

    @Test
    public void sendTextMsg() {
        GroupBot.sendTextMsg(GROUP_BOT_KEY, "构建知会：" + new Date());
        GroupBot.sendTextMsg(GROUP_BOT_KEY, "构建知会 For mentioned_mobile：" + new Date(), null, List.of("13167019278"));
        GroupBot.sendTextMsg(GROUP_BOT_KEY, "构建知会 For mention userid：" + new Date(), List.of("HB266"), null);
        GroupBot.sendTextMsg(GROUP_BOT_KEY, "构建知会 For mention everyone：" + new Date(), List.of("@all"), null);
    }

    @Test
    public void testSendImageMsg() throws IOException {
        GroupBot.sendImageMsg(GROUP_BOT_KEY, IOUtils.toByteArray(GroupBotTest.class.getResourceAsStream("/cat.jpg")));
    }

    @Test
    public void testSendNewsMsg() {
        List<GroupBot.NewsArticle> articles = List.of(new GroupBot.NewsArticle().setTitle("grus-build").setDescription("desc").setUrl("https://www.baidu.com").setPicUrl("http://res.mail.qq.com/node/ww/wwopenmng/images/independent/doc/test_pic_msg1.png"));
        GroupBot.sendNewsMsg(GROUP_BOT_KEY, articles);
    }
}
