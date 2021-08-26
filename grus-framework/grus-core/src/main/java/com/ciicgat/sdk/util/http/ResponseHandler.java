/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;

import okhttp3.Response;

import java.util.function.Function;

/**
 * Created by August.Zhou on 2019-02-20 11:29.
 */
public interface ResponseHandler<T> extends Function<Response, T> {

    @Override
    T apply(Response response);
}
