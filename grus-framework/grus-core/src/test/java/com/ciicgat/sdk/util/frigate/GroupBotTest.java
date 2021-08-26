/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import java.util.Date;

/**
 * Created by Albert on 2019-05-08.
 */
public class GroupBotTest {

    /**
     * 企业微信机器人的 key，用来发送报警通知
     */
    private static final String GROUP_BOT_KEY = "your own group bot key";

    //    @Test
    public void sendMarkdownMsg() {

        StringBuffer markdown = new StringBuffer();
        markdown.append("# Grus 构建知会").append("\n");
        markdown.append(">").append("警告:").append("<font color=\\\"warning\\\">这是警告（黄色）</font>").append("\n");
        markdown.append(">").append("提示:").append("<font color=\\\"info\\\">这是提示（绿色）</font>").append("\n");
        markdown.append(">").append("注释:").append("<font color=\\\"comment\\\">这是备注（灰色）</font>").append("\n");
        markdown.append(">").append(new Date());

        GroupBot.sendMarkdownMsg(GROUP_BOT_KEY, markdown.toString());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    //    @Test
    public void sendTextMsg() {

        GroupBot.sendTextMsg(GROUP_BOT_KEY, "构建知会：" + new Date());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
