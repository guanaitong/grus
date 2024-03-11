/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq.processor;

import com.ciicgat.grus.rabbitmq.Message;
import com.ciicgat.grus.rabbitmq.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by August.Zhou on 2022/7/8 13:24
 */
public class DispatcherMsgProcessor implements MsgProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherMsgProcessor.class);
    private final Map<String, ? extends MsgProcessor> processors;

    public DispatcherMsgProcessor(Map<String, ? extends MsgProcessor> msgProcessorMap) {
        this.processors = msgProcessorMap;
    }

    @Override
    public State onMessage(Message msg) {
        MsgProcessor msgProcessor = processors.get(msg.getQueue());
        if (msgProcessor == null) {
            LOGGER.error("cannot find processor");
            return State.Abort;
        }
        return msgProcessor.onMessage(msg);
    }
}
