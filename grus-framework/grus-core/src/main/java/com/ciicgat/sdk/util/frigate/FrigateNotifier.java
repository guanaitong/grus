/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import com.ciicgat.grus.core.Module;
import com.ciicgat.sdk.util.StackUtil;
import com.ciicgat.sdk.util.system.Systems;

import java.util.Map;
import java.util.Objects;

import static com.ciicgat.sdk.util.frigate.FrigateClient.send;

/**
 * Frigate 通知器
 * <p>
 * 1. 支持指定工号，指定组， detect 应用
 * 2. 站内信、企业微信、邮箱、短信及组合
 * <p>
 * Created by Albert on 2018/10/29.
 */
public abstract class FrigateNotifier {

    private FrigateNotifier() {

    }

    /**
     * 支持指定 channel，消息内容、堆栈内容及可变员工工号进行消息发送。
     *
     * @param channel
     * @param content   完整消息内容
     * @param throwable 错误堆栈信息
     * @param empNos    员工工号可变参数可指定多个
     */
    public static void sendMessage(NotifyChannel channel, String content, Throwable throwable, String... empNos) {
        FrigateMessage frigateMessage = buildMsg(channel, content, throwable);
        send(LocalFrigateMessageConstants.DEFAULT.getByQiWeiXinUrl(), Map.of("receiveWeChatIds", String.join(",", empNos)), frigateMessage);
    }


    /**
     * 发送信息给项目组ID，可同时把一条消息发送给该组下的所有成员，可指定渠道。
     *
     * @param channel   需要发送的渠道
     * @param groupId
     * @param content
     * @param throwable
     */
    public static void sendMessageToGroupId(NotifyChannel channel, Integer groupId, String content, Throwable throwable) {
        if (groupId == null || groupId < 1) {
            return;
        }
        FrigateMessage frigateMessage = buildMsg(channel, content, throwable);
        send(LocalFrigateMessageConstants.DEFAULT.getByGroupUrl(), Map.of("receiveGroups", String.valueOf(groupId)), frigateMessage);
    }


    /**
     * 自动 detect 应用（应用名不需要传进来，系统根据环境变量自动获取），发送消息给应用的开发者。
     *
     * @param content
     */
    public static void sendMessageByAppName(String content) {
        sendMessageByAppName(NotifyChannel.QY_WE_CHAT, content, null);
    }

    /**
     * 自动 detect 应用（应用名不需要传进来，系统根据环境变量自动获取），发送消息给应用的开发者。 默认渠道企业微信发送
     *
     * @param content
     * @param throwable
     */
    public static void sendMessageByAppName(String content, Throwable throwable) {
        sendMessageByAppName(NotifyChannel.QY_WE_CHAT, content, throwable);
    }


    /**
     * 自动 detect 应用（应用名不需要传进来，系统根据环境变量自动获取），发送消息给应用的开发者。
     *
     * @param channel
     * @param content
     * @param throwable
     */
    public static void sendMessageByAppName(NotifyChannel channel, String content, Throwable throwable) {
        if (Objects.equals(Systems.APP_NAME, Systems.UNKNOWN)) { //应用名未知时，不发送
            return;
        }
        FrigateMessage frigateMessage = buildMsg(channel, content, throwable);
        send(LocalFrigateMessageConstants.DEFAULT.getByAppNameUrl(), Map.of("receiveAppNames", Systems.APP_NAME), frigateMessage);
    }

    /**
     * 指定模块发送消息
     *
     * @param channel
     * @param module    模块
     * @param content
     * @param throwable
     */
    public static void sendMessageByAppName(NotifyChannel channel, Module module, String content, Throwable throwable) {
        if (Objects.equals(Systems.APP_NAME, Systems.UNKNOWN)) { //应用名未知时，不发送
            return;
        }
        FrigateMessage frigateMessage = buildMsg(channel, content, throwable);
        frigateMessage.setModule(module.getName());
        send(LocalFrigateMessageConstants.DEFAULT.getByAppNameUrl(), Map.of("receiveAppNames", Systems.APP_NAME), frigateMessage);
    }

    /**
     * 支持按渠道发送消息
     *
     * @param channel
     * @param content
     * @param throwable
     * @return
     */
    private static FrigateMessage buildMsg(NotifyChannel channel, String content, Throwable throwable) {
        FrigateMessage frigateMessage = FrigateMessage.newInstance();
        if (null == channel) {
            channel = NotifyChannel.DEFAULT;
        }
        frigateMessage.setChannel(channel.code);
        frigateMessage.setContent(content == null ? "" : content);
        if (throwable != null) {
            frigateMessage.setStack(StackUtil.getStack(throwable));
        }

        return frigateMessage;
    }


}
