/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.elasticsearch.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by August.Zhou on 2019-09-09 13:01.
 */
public class DefaultEntityMapper<T extends IndexAble> implements EntityMapper<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();


    public DefaultEntityMapper() {
        objectMapper.registerModule(new ElasticsearchModule());

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        //属性为 空字符串 或者为 NULL 都不序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    }


    @Override
    public String mapToString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new EsDataException(e);
        }
    }

    @Override
    public T mapToObject(String source, Class<T> clazz) {
        try {
            return objectMapper.readValue(source, clazz);
        } catch (IOException e) {
            throw new EsDataException(e);
        }
    }
}
