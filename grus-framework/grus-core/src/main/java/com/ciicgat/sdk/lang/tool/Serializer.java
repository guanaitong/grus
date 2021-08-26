/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lang.tool;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Base64;

/**
 * Created by August.Zhou on 2016/12/30 17:11.
 */
public interface Serializer {
    /**
     * 使用java的序列化请注意，需要加版本：private static final long serialVersionUID = 1L;
     * 不然，会根据当前类的结构，hash出一个versionId。如果类有变化(增删改成员变量)，那么versionId也会变化，
     * versionId不同后，原来老的数据，就会反序列化失败。
     */
    Serializer JAVA = new Serializer() {
        @Override
        public String serialize(Object object) {
            return Base64.getEncoder().encodeToString(serializeToBytes(object));
        }

        @Override
        public <T> T deserialize(String text, Class<T> tClass) {
            if (text == null || text.isEmpty()) {
                return null;
            }
            return deserializeFromBytes(Base64.getDecoder().decode(text), tClass);
        }

        @Override
        public byte[] serializeToBytes(Object object) {
            return SerializationUtils.serialize((Serializable) object);
        }

        @Override
        public <T> T deserializeFromBytes(byte[] bytes, Class<T> tClass) {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            return SerializationUtils.deserialize(bytes);
        }


    };
    Serializer JSON = new Serializer() {
        @Override
        public String serialize(Object object) {
            return com.ciicgat.grus.json.JSON.toJSONString(object);
        }

        @Override
        public <T> T deserialize(String text, Class<T> tClass) {
            if (text == null || text.isEmpty()) {
                return null;
            }
            return com.ciicgat.grus.json.JSON.parse(text, tClass);
        }

        @Override
        public byte[] serializeToBytes(Object object) {
            return Bytes.toBytes(com.ciicgat.grus.json.JSON.toJSONString(object));
        }

        @Override
        public <T> T deserializeFromBytes(byte[] bytes, Class<T> tClass) {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            return com.ciicgat.grus.json.JSON.parse(Bytes.toString(bytes), tClass);
        }
    };


    String serialize(Object object);


    <T> T deserialize(String text, Class<T> tClass);

    byte[] serializeToBytes(Object object);

    <T> T deserializeFromBytes(byte[] bytes, Class<T> tClass);


}
