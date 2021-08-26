/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http.metrics;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by August.Zhou on 2019-12-09 14:49.
 */
public class DelegateEventListener extends EventListener {
    private EventListener eventListener = EventListener.NONE;

    DelegateEventListener() {
    }

    public DelegateEventListener setEventListener(EventListener eventListener) {
        if (eventListener != null) {
            this.eventListener = eventListener;
        }
        return this;
    }

    @Override
    public void callStart(Call call) {
        eventListener.callStart(call);
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        eventListener.requestHeadersEnd(call, request);
    }

    @Override
    public void callFailed(Call call, IOException e) {
        eventListener.callFailed(call, e);
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        eventListener.responseHeadersEnd(call, response);
    }

    static DelegateEventListener FOR_CORE = new DelegateEventListener();

    static DelegateEventListener FOR_FEIGN = new DelegateEventListener();

    public static DelegateEventListener getForCoreInstance() {
        return FOR_CORE;
    }

    public static DelegateEventListener getForFeignInstance() {
        return FOR_FEIGN;
    }
}
