/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http.trace;

import io.opentracing.propagation.TextMap;
import okhttp3.Request;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by August.Zhou on 2018/8/22 17:40.
 */
public class RequestBuilderInjectAdapter implements TextMap {

    private Request.Builder requestBuilder;

    public RequestBuilderInjectAdapter(Request.Builder request) {
        this.requestBuilder = request;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("Should be used only with tracer#inject()");
    }

    @Override
    public void put(String key, String value) {
        requestBuilder.addHeader(key, value);
    }
}
