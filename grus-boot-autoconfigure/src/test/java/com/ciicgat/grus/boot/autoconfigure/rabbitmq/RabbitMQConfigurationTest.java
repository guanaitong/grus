/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.rabbitmq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Created by August.Zhou on 2022/7/8 09:49
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.application.name=grus-demo", "grus.rabbitmq.consumer.enable=true", "grus.rabbitmq.useSingleConnection=false", "spring.quartz.autoStartup=false"})
public class RabbitMQConfigurationTest {

    @Autowired
    private TestMsgProcessor testMsgProcessor;

    @Test
    public void testProcessor() {

    }


}
