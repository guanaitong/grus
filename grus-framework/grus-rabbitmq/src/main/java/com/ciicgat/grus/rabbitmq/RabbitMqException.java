/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

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

    public static void handleException(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else {
            throw new RabbitMqException(e);
        }
    }

}
