/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Created by August.Zhou on 2023/12/1 15:22
 */
class RabbitMqExceptionTest {

    @Test
    void convert() {
        Assertions.assertEquals(RabbitMqException.class, RabbitMqException.convert("123", new IOException(new RuntimeException())).getClass());
        Assertions.assertEquals(RabbitMqException.class, RabbitMqException.convert("123", new IOException()).getClass());
    }

    @Test
    void handleException() {
        Assertions.assertThrows(RabbitMqException.class, () -> RabbitMqException.handleException(new IOException(new RuntimeException())));
        Assertions.assertThrows(RuntimeException.class, () -> RabbitMqException.handleException(new RuntimeException()));
        Assertions.assertThrows(RabbitMqException.class, () -> RabbitMqException.handleException(new Exception()));
    }
}
