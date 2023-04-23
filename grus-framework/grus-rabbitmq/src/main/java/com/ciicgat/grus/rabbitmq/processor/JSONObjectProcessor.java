/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq.processor;

import com.ciicgat.grus.json.JSON;
import com.ciicgat.grus.rabbitmq.Message;
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
public abstract class JSONObjectProcessor<T> implements ObjectMsgProcessor<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONObjectProcessor.class);
    private final JavaType _type;

    public JSONObjectProcessor() {
        Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this._type = JSON.getTypeFactory().constructType(type);
    }

    @Override
    public final T convert(Message message) {
        return JSON.parse(message.getBody(), _type);
    }
}
