/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.core.Module;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

/**
 * Created by August.Zhou on 2023/12/21 12:42
 */
public class AlertExceptionHandler extends DefaultExceptionHandler {

    @Override
    protected void log(String message, Throwable e) {
        super.log(message, e);
        Alert.send("严重警报：消息队列可能断开连接", e);
        FrigateNotifier.sendMessageByAppName(NotifyChannel.SMS, Module.RABBITMQ, "严重警报：消息队列可能断开连接", e);
    }
}
