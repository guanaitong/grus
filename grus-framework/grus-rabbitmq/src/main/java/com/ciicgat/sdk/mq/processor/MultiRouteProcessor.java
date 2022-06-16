/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq.processor;

import com.ciicgat.sdk.mq.MsgProcessor;
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
    private Map<String, SingleRouteProcessor<?>> processors;

    public MultiRouteProcessor(Map<String, SingleRouteProcessor<?>> processors) {
        this.processors = Collections.unmodifiableMap(Objects.requireNonNull(processors));
    }

    @Override
    public final State apply(String msgId, String routingKey, String msg) {
        SingleRouteProcessor<?> singleRouteProcessor = processors.get(routingKey);
        if (singleRouteProcessor == null) {
            LOGGER.error("cannot find processor");
            return State.Abort;
        }
        return singleRouteProcessor.apply(msgId, routingKey, msg);
    }

}
