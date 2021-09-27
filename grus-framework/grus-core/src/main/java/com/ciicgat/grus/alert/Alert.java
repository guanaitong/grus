/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
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

    public static void send(String content) {
        send(content, null);
    }

    public static void send(String content, Throwable throwable) {
        FrigateMessage frigateMessage = FrigateMessage.newInstance();

        frigateMessage.setContent(content == null ? "" : content);
        if (throwable != null) {
            frigateMessage.setStack(StackUtil.getStack(throwable));
        }
        if (GroupBot.GLOBAL_GROUP_KEY != null) {
            frigateMessage.setChannel(NotifyChannel.DEFAULT.code());
            GroupBot.sendTextMsg(GroupBot.GLOBAL_GROUP_KEY, frigateMessage.toFormatText());
        } else {
            frigateMessage.setChannel(NotifyChannel.QY_WE_CHAT.code());
        }
        FrigateRawNotifier.sendMsgByAppNames(List.of(Systems.APP_NAME), frigateMessage);
    }

}
