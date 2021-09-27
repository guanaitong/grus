/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static com.ciicgat.sdk.util.frigate.MsgClient.send;

/**
 * Created by August.Zhou on 2019-09-18 17:07.
 */
public abstract class FrigateRawNotifier implements FrigateMessageConstants {

    /**
     * 发送信息给项目组ID，可同时把一条消息发送给该组下的所有成员，可指定渠道。
     *
     * @param receiveGroups  接受的组
     * @param frigateMessage 消息体
     */
    public static void sendMsgByGroups(List<Integer> receiveGroups, FrigateMessage frigateMessage) {
        send(GROUP_PATH, Map.of("receiveGroups", StringUtils.join(receiveGroups, ",")), frigateMessage);
    }

    /**
     * 发送信息给项目组ID，可同时把一条消息发送给该组下的所有成员，可指定渠道。
     *
     * @param empNos         接受的工号
     * @param frigateMessage 消息体
     */
    public static void sendMsgByWeChatIds(List<String> empNos, FrigateMessage frigateMessage) {
        send(QIYE_WECHAT_PATH, Map.of("receiveWeChatIds", StringUtils.join(empNos, ",")), frigateMessage);
    }

    /**
     * 发送信息给项目组ID，可同时把一条消息发送给该组下的所有成员，可指定渠道。
     *
     * @param receiveAppNames 接受的应用
     * @param frigateMessage  消息体
     */
    public static void sendMsgByAppNames(List<String> receiveAppNames, FrigateMessage frigateMessage) {
        send(APP_NAME_PATH, Map.of("receiveAppNames", StringUtils.join(receiveAppNames, ",")), frigateMessage);
    }
}
