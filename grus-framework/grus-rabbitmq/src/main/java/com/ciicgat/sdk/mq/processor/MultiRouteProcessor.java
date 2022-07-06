/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq.processor;

import com.ciicgat.sdk.mq.Msg;
import com.ciicgat.sdk.mq.MsgProcessor;
import com.ciicgat.sdk.mq.QueueKey;
import com.ciicgat.sdk.mq.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Created by August.Zhou on 2022/7/3 09:40
 */
public class MultiRouteProcessor implements MsgProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiRouteProcessor.class);
    private Map<QueueKey, SingleRouteProcessor<?>> processors;

    /**
     * @param processors
     */
    public MultiRouteProcessor(Map<QueueKey, SingleRouteProcessor<?>> processors) {
        this.processors = Collections.unmodifiableMap(Objects.requireNonNull(processors));
    }

    @Override
    public State apply(Msg msg) {
        SingleRouteProcessor<?> singleRouteProcessor = processors.get(new QueueKey(msg.routingKey(), msg.queue()));
        if (singleRouteProcessor == null) {
            LOGGER.error("cannot find processor");
            return State.Abort;
        }
        return singleRouteProcessor.apply(msg);
    }
}
