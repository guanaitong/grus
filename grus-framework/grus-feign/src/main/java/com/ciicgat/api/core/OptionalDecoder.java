/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import feign.Response;
import feign.Util;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * 原始的feign.optionals.OptionalDecoder不能满足我们的需求
 * Created by August.Zhou on 2018-10-30 13:14.
 */
class OptionalDecoder implements Decoder {
    final Decoder delegate;

    OptionalDecoder(Decoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (!isOptional(type)) {
            return delegate.decode(response, type);
        }
        if (response.status() == 204) { //No Content
            return Optional.empty();
        }
        Type enclosedType = Util.resolveLastTypeParameter(type, Optional.class);
        //原先为Optional.of(delegate.decode(response, enclosedType));
        //但是，我们里面的值对于我们“code、msg、data”来说，data可能为null
        //所以需要改为ofNullable
        return Optional.ofNullable(delegate.decode(response, enclosedType));
    }

    static boolean isOptional(Type type) {
        if (!(type instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        return parameterizedType.getRawType().equals(Optional.class);
    }
}

