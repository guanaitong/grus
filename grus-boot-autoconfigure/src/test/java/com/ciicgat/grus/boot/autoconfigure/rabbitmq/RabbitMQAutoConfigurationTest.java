/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.rabbitmq;

import com.ciicgat.grus.boot.autoconfigure.condition.ServerEnvCondition;
import com.ciicgat.grus.boot.autoconfigure.core.GrusCoreContextInitializer;
import com.ciicgat.sdk.mq.RabbitMqConsumer;
import com.ciicgat.sdk.mq.RabbitMqProducer;
import com.rabbitmq.client.Connection;
import io.opentelemetry.api.GlobalOpenTelemetry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Created by August.Zhou on 2022/7/8 09:49
 */
public class RabbitMQAutoConfigurationTest {
    static {
        ServerEnvCondition.isTest = true;
        GlobalOpenTelemetry.resetForTest();
    }

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new GrusCoreContextInitializer())
            .withUserConfiguration(RabbitMQAutoConfiguration.class);

    @Test
    public void testInit() {
        this.contextRunner
                .withSystemProperties()
                .withPropertyValues("spring.application.name=grus-demo", "grus.rabbitmq.init.enable=true",
                        "grus.rabbitmq.init.exchanges[0].name=grus-demo-test-exchange",
                        "grus.rabbitmq.init.exchanges[0].type=direct",
                        "grus.rabbitmq.init.exchanges[1].name=global.dead.exchange",
                        "grus.rabbitmq.init.exchanges[1].type=fanout",
                        "grus.rabbitmq.init.queues[0].name=grus-demo-test-queue",
                        "grus.rabbitmq.init.queues[0].args.x-dead-letter-exchange=global.dead.exchange",
                        "grus.rabbitmq.init.queues[0].args.x-dead-letter-routing-key=",
                        "grus.rabbitmq.init.queues[0].exclusive=false",
                        "grus.rabbitmq.init.queues[0].autoDelete=false",
                        "grus.rabbitmq.init.binds[0].queue=grus-demo-test-queue",
                        "grus.rabbitmq.init.binds[0].exchange=grus-demo-test-exchange",
                        "grus.rabbitmq.init.binds[0].routing_key=grus-demo-test")
                .run(context -> {
                    RabbitMQAutoConfiguration.InitConfiguration connection = context.getBean(RabbitMQAutoConfiguration.InitConfiguration.class);
                    assertThat(connection).isInstanceOf(RabbitMQAutoConfiguration.InitConfiguration.class);
                });
    }

    @Test
    public void testInitError() {
        this.contextRunner
                .withSystemProperties()
                .withPropertyValues("spring.application.name=grus-demo", "grus.rabbitmq.init.enable=true",
                        "grus.rabbitmq.init.exchanges[0].name=grus-demo-test-exchange2",
                        "grus.rabbitmq.init.exchanges[0].type=1")
                .run(context -> {
                    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> context.getBean(RabbitMQAutoConfiguration.InitConfiguration.class));
                });
    }

    @Test
    public void testProducer() {
        this.contextRunner
                .withSystemProperties()
                .withPropertyValues("spring.application.name=grus-demo", "grus.rabbitmq.producer.enable=true", "grus.rabbitmq.useSingleConnection=false")
                .run(context -> {
                    Connection connection = context.getBean("producerConnection", Connection.class);
                    assertThat(connection).isInstanceOf(Connection.class);
                    RabbitMqProducer rabbitMqProducer = context.getBean(RabbitMqProducer.class);
                    assertThat(rabbitMqProducer).isInstanceOf(RabbitMqProducer.class);
                });
    }

    //    @Test
    public void testConsumer() {
        this.contextRunner
                .withSystemProperties()
                .withPropertyValues("spring.application.name=grus-demo", "grus.rabbitmq.consumer.enable=true")
                .run(context -> {
                    Connection connection = context.getBean("consumerConnection", Connection.class);
                    assertThat(connection).isInstanceOf(Connection.class);
                    RabbitMqConsumer rabbitMqConsumer = context.getBean(RabbitMqConsumer.class);
                    assertThat(rabbitMqConsumer).isInstanceOf(RabbitMqConsumer.class);
                });
    }

}
