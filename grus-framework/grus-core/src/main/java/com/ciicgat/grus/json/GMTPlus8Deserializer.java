/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * @author wanchongyang
 * @date 2021/5/27 2:19 下午
 */
public class GMTPlus8Deserializer extends JsonDeserializer<Date> {
    private static final long OFFSET_TIME = 8 * 3600 * 1000L;

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return new Date(jsonParser.getLongValue() - OFFSET_TIME);
    }
}
