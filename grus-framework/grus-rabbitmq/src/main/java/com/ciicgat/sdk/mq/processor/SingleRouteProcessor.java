/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq.processor;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.json.JSONException;
import com.ciicgat.sdk.mq.MsgProcessor;
import com.ciicgat.sdk.mq.State;
import com.fasterxml.jackson.databind.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * exchange通过单路由到单队列。此种情况，routingKey只有一个，对应的对象按照规范也只有一种。
 * <p>
 * Created by August.Zhou on 2022/7/3 09:40
 */
public abstract class SingleRouteProcessor<T> implements MsgProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleRouteProcessor.class);
    private final JavaType _type;

    public SingleRouteProcessor() {
        Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this._type = JSON.getTypeFactory().constructType(type);
    }

    @Override
    public final State apply(String msgId, String routingKey, String msg) {
        Object value;
        try {
            value = JSON.parse(msg, _type);
        } catch (JSONException jsonException) {
            LOGGER.error("json parse error", jsonException);
            return State.Abort;
        }
        return handle(routingKey, (T) value);
    }

    protected abstract State handle(String routingKey, T object);
}
