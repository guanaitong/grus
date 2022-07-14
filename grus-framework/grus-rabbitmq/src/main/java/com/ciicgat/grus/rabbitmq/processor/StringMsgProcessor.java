/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq.processor;

import com.ciicgat.grus.rabbitmq.Message;
import com.ciicgat.grus.rabbitmq.State;

import java.nio.charset.StandardCharsets;

/**
 * Created by August.Zhou on 2022/7/14 11:38
 */
public interface StringMsgProcessor extends ObjectMsgProcessor<String> {

    @Override
    default String convert(Message message) {
        return new String(message.getBody(), StandardCharsets.UTF_8);
    }

    @Override
    State onMessage(Message message, String value);
}
