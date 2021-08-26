/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.http;


import java.net.Socket;
import java.time.Duration;
import java.util.Objects;


/**
 * Created by August.Zhou on 2019-02-20 11:38.
 */
public class HttpTimeout {
    Duration connectTimeout = Duration.ofSeconds(10);
    Duration readTimeout = Duration.ofSeconds(180);
    Duration writeTimeout = Duration.ofSeconds(60);

    public HttpTimeout() {
    }

    /**
     * Sets the default connect timeout for new connections. A value of 0 means no timeout,
     * otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to
     * milliseconds.
     *
     * <p>The connect timeout is applied when connecting a TCP socket to the target host.
     * The default value is 10 seconds.
     */
    public HttpTimeout connectTimeout(Duration connectTimeout) {
        this.connectTimeout = Objects.requireNonNull(connectTimeout);
        return this;
    }

    /**
     * Sets the default read timeout for new connections. A value of 0 means no timeout, otherwise
     * values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
     *
     * <p>The read timeout is applied to both the TCP socket and for individual read IO operations
     * including on {Source} of the {Response}. The default value is 10 seconds.
     *
     * @see Socket#setSoTimeout(int)
     */
    public HttpTimeout readTimeout(Duration readTimeout) {
        this.readTimeout = Objects.requireNonNull(readTimeout);
        return this;
    }

    /**
     * Sets the default write timeout for new connections. A value of 0 means no timeout, otherwise
     * values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
     *
     * <p>The write timeout is applied for individual write IO operations.
     * The default value is 10 seconds.
     */
    public HttpTimeout writeTimeout(Duration writeTimeout) {
        this.writeTimeout = Objects.requireNonNull(writeTimeout);
        return this;
    }

}
