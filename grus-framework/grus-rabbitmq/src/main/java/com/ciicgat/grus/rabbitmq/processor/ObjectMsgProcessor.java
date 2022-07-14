/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq.processor;

import com.ciicgat.grus.rabbitmq.Message;
import com.ciicgat.grus.rabbitmq.State;

/**
 * Created by August.Zhou on 2022/7/14 12:51
 */
public interface ObjectMsgProcessor<T> extends MsgProcessor {
    @Override
    default State onMessage(Message message) {
        T value = convert(message);
        return onMessage(message, value);
    }

    T convert(Message message);

    State onMessage(Message message, T value);
}
