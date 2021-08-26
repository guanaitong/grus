/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq.trace;

import io.opentracing.propagation.TextMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class MapHeadersAdapter implements TextMap {

    private Map<String, Object> objectMap;

    public MapHeadersAdapter(Map<String, Object> objectMap) {
        this.objectMap = Objects.requireNonNull(objectMap);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        HashMap<String, String> stringMap = new HashMap<>();
        objectMap.forEach((key, value) -> stringMap.put(key, String.valueOf(value)));
        return stringMap.entrySet().iterator();
    }

    @Override
    public void put(String key, String value) {
        objectMap.put(key, value);
    }
}
