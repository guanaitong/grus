/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.rabbitmq;

import com.ciicgat.grus.rabbitmq.Message;
import com.ciicgat.grus.rabbitmq.State;
import com.ciicgat.grus.rabbitmq.processor.MsgProcessor;

/**
 * Created by August.Zhou on 2022/7/8 16:17
 */
@MessageListener("grus-demo-test-queue")
public class TestMsgProcessor implements MsgProcessor {
    @Override
    public State apply(Message message) {
        return State.OK;
    }
}
