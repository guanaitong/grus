/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf.remote;

import java.util.Map;

/**
 * Created by August.Zhou on 2020-04-26 10:44.
 */
class JsonValueWrapper extends ValueWrapper {

    private final Map<String, Object> jsonObject;
    private Object asBeanCache;


    JsonValueWrapper(String value, Map<String, Object> jsonObject) {
        super(value);
        this.jsonObject = jsonObject;
    }


    @Override
    public Map<String, Object> asJSONObject() {
        return jsonObject;
    }

    @Override
    public Object asBean(Class<?> clazz) {
        if (asBeanCache == null) {
            Object v = InnerJson.parse(value, clazz);
            validate(v);
            this.asBeanCache = v;
        }
        return asBeanCache;
    }
}
