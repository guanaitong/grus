/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.alert;

import com.ciicgat.sdk.util.StackUtil;
import com.ciicgat.sdk.util.frigate.FrigateMessage;
import com.ciicgat.sdk.util.frigate.FrigateRawNotifier;
import com.ciicgat.sdk.util.frigate.GroupBot;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import com.ciicgat.sdk.util.system.Systems;

import java.util.List;

/**
 * Created by August.Zhou on 2021/9/27 13:19.
 */
public class Alert {

    private static final int STACK_MAX_LENGTH = 1024;

    public static void send(String content) {
        send(content, null);
    }

    public static void send(String content, Throwable throwable) {
        send(content, throwable, GroupBot.GLOBAL_GROUP_KEY, AlertLevel.NORMAL);
    }

    public static void send(String content, Throwable throwable, AlertLevel level) {
        send(content, throwable, GroupBot.GLOBAL_GROUP_KEY, level);

    }

    public static void send(String content, Throwable throwable, String botKey, AlertLevel level) {
        FrigateMessage frigateMessage = FrigateMessage.newInstance();

        frigateMessage.setContent(content == null ? "" : content);
        if (throwable != null) {
            frigateMessage.setStack(StackUtil.getStack(throwable));
        }
        if (botKey != null) {
            frigateMessage.setChannel(NotifyChannel.DEFAULT.code());
            if (level == AlertLevel.NORMAL) {
                GroupBot.sendTextMsg(botKey, frigateMessage.toFormatText());
            } else {
                GroupBot.sendMarkdownMsg(botKey, buildMarkDown(frigateMessage, level));
            }
        } else {
            frigateMessage.setChannel(NotifyChannel.QY_WE_CHAT.code());
        }
        FrigateRawNotifier.sendMsgByAppNames(List.of(Systems.APP_NAME), frigateMessage);
    }

    private static String buildMarkDown(FrigateMessage frigateMessage, AlertLevel level) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# ").append(level.title).append("\n");
        markdown.append(">").append("<font color=\"").append(level.color).append("\">")
                .append(frigateMessage.getContent())
                .append("</font>").append("\n");
        if (frigateMessage.getStack() != null) {
            String stack = frigateMessage.getStack().length() > STACK_MAX_LENGTH ? frigateMessage.getStack().substring(0, STACK_MAX_LENGTH) : frigateMessage.getStack();
            markdown.append("> 异常堆栈：").append(stack);
        }

        return markdown.toString();
    }

}
