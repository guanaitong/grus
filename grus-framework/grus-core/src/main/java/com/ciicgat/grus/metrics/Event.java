/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.metrics;

import java.io.Closeable;

public interface Event extends Closeable {

    /**
     * send error msg to tracer and frigate_messager
     *
     * @param content
     * @param throwable
     */
    void error(String content, Throwable throwable);

    Event NOOP = new Event() {

        @Override
        public void close() {

        }

        @Override
        public void error(String content, Throwable throwable) {

        }
    };

}
