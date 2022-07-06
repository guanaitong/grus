/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.rabbitmq;

import com.ciicgat.sdk.mq.Message;
import com.ciicgat.sdk.mq.MsgProcessor;
import com.ciicgat.sdk.mq.State;

/**
 * Created by August.Zhou on 2022/7/8 16:17
 */
@MessageQueueListener("grus-demo-test-queue")
public class TestMsgProcessor implements MsgProcessor {
    @Override
    public State apply(Message message) {
        return State.OK;
    }
}
