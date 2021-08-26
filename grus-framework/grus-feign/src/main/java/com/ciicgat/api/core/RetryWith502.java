/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.Retryer;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.util.Date;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by August.Zhou on 2018-11-30 15:56.
 */
class RetryWith502 {

    static class RetryableException502 extends RetryableException {

        RetryableException502(int status, String message, Request.HttpMethod httpMethod, Date retryAfter) {
            super(status, message, httpMethod, retryAfter);
        }
    }

    static class ErrorDecoderImpl implements ErrorDecoder {

        private final ErrorDecoder decoder = new ErrorDecoder.Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            if (response.status() == 502) {
                String message = format("status %s reading %s", response.status(), methodKey);
                try {
                    if (response.body() != null) {
                        String body = Util.toString(response.body().asReader());
                        message += "; content:\n" + body;
                    }
                } catch (IOException ignored) { // NOPMD
                }
                return new RetryableException502(response.status(), message, response.request().httpMethod(), new Date());
            }
            return decoder.decode(methodKey, response);
        }
    }

    static class RetryerImpl extends Retryer.Default {

        RetryerImpl() {
            super(100, SECONDS.toMillis(1), 5);
        }


        @Override
        public void continueOrPropagate(RetryableException e) {
            if (e instanceof RetryableException502) {
                super.continueOrPropagate(e);
            } else {
                throw e;
            }
        }

        @Override
        public Retryer clone() {
            return new RetryerImpl();
        }
    }
}
