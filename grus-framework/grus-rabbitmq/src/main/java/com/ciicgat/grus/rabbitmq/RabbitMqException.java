/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import java.io.IOException;

/**
 * Created by August.Zhou on 2022/7/2 20:02
 */
public class RabbitMqException extends RuntimeException {

    public RabbitMqException() {
    }

    public RabbitMqException(String message) {
        super(message);
    }

    public RabbitMqException(String message, Throwable cause) {
        super(message, cause);
    }

    public RabbitMqException(Throwable cause) {
        super(cause);
    }

    public static RabbitMqException convert(String message, IOException e) {
        if (e.getCause() != null) {
            return new RabbitMqException(message, e.getCause());
        }
        return new RabbitMqException(message, e);
    }

    public static void handleException(Exception e) {
        if (e instanceof IOException) {
            if (e.getCause() != null) {
                throw new RabbitMqException(e.getCause());
            }
        } else if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        throw new RabbitMqException(e);
    }

}
