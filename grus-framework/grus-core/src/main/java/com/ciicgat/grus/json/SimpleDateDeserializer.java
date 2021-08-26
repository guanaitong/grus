/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * @author August Zhou
 * @date 2017/8/1 14:18
 */
public class SimpleDateDeserializer extends JsonDeserializer<Date> {

    private static final String[] PARSE_PATTERNS = new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"};

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        int tokenId = jp.getCurrentTokenId();
        if (tokenId == JsonTokenId.ID_NUMBER_INT) {
            return new Date(jp.getValueAsLong());
        }
        try {
            String value = jp.getValueAsString();
            return value == null ? null : DateUtils.parseDate(value, PARSE_PATTERNS);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }
}
