/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Optional;

/**
 * @author : August
 * @date 2020/3/23 13:20
 */
public class JSON {
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setConfig(OBJECT_MAPPER.getDeserializationConfig().with(new StdDateFormatExtend()))
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    /**
     * 将对象输出为json字符串
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return toJSONString(object, false);
    }

    /**
     * 将对象输出为json字符串
     *
     * @param object       对象
     * @param prettyFormat 美化格式
     * @return
     */
    public static String toJSONString(Object object, boolean prettyFormat) {
        try {
            if (prettyFormat) {
                return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            }
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * 解析为指定类
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parse(String text, Class<T> clazz) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(text, clazz);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * 用于处理带泛型嵌套的类
     *
     * @param text
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T parse(String text, TypeReference<T> typeReference) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(text, typeReference);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * 用于处理带泛型嵌套的类
     *
     * @param text
     * @param javaType
     * @param <T>
     * @return
     */
    public static <T> T parse(String text, JavaType javaType) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(text, javaType);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }


    /**
     * 解析为通用的树的数据结构，不用和对象做映射
     *
     * @param text
     * @return
     */
    public static JsonNode parse(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(text);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * 用于将jsonNode转换为java对象
     *
     * @param jsonNode
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T toJavaObject(JsonNode jsonNode, Class<T> tClass) {
        try {
            JsonParser jsonParser = OBJECT_MAPPER.treeAsTokens(jsonNode);
            return OBJECT_MAPPER.readValue(jsonParser, tClass);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * 用于将jsonNode转换为java对象
     *
     * @param jsonNode
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T toJavaObject(JsonNode jsonNode, TypeReference<T> typeReference) {
        try {
            JsonParser jsonParser = OBJECT_MAPPER.treeAsTokens(jsonNode);
            return OBJECT_MAPPER.readValue(jsonParser, typeReference);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * Accessor for getting currently configured {@link TypeFactory} instance.
     */
    public static TypeFactory getTypeFactory() {
        return OBJECT_MAPPER.getTypeFactory();
    }

    /**
     * 用于将jsonNode转换为java对象
     *
     * @param jsonNode
     * @param javaType
     * @param <T>
     * @return
     */
    public static <T> T toJavaObject(JsonNode jsonNode, JavaType javaType) {
        try {
            JsonParser jsonParser = OBJECT_MAPPER.treeAsTokens(jsonNode);
            return OBJECT_MAPPER.readValue(jsonParser, javaType);
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    /**
     * 方便处理jsonNode为NullNode情况
     *
     * @param jsonNode
     * @return
     */
    public static Optional<JsonNode> of(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return Optional.empty();
        }
        return Optional.of(jsonNode);
    }
}
