/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.rabbitmq;

import com.ciicgat.grus.boot.autoconfigure.condition.ConditionalOnWorkEnv;
import com.ciicgat.grus.rabbitmq.ConnectionFactory;
import com.ciicgat.grus.rabbitmq.Exchange;
import com.ciicgat.grus.rabbitmq.Queue;
import com.ciicgat.grus.rabbitmq.QueueBind;
import com.ciicgat.grus.rabbitmq.RabbitMqConsumer;
import com.ciicgat.grus.rabbitmq.RabbitMqException;
import com.ciicgat.grus.rabbitmq.RabbitMqInitializer;
import com.ciicgat.grus.rabbitmq.RabbitMqProducer;
import com.ciicgat.sdk.util.system.WorkEnv;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created by August.Zhou on 2022/7/7 12:17
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RabbitMQProperty.class)
@ConditionalOnClass({RabbitMqProducer.class, Connection.class})
public class RabbitMQAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQAutoConfiguration.class);

    public RabbitMQAutoConfiguration(RabbitMQProperty rabbitMQProperty) {
        if (rabbitMQProperty.getInit().isEnable()) {
            LOGGER.info("start rabbitMQ init");
            init(rabbitMQProperty);
            LOGGER.info("end rabbitMQ init");
        }
    }

    private void init(RabbitMQProperty rabbitMQProperty) {
        try (Connection connection = ConnectionFactory.newConnection(rabbitMQProperty.getHost(), rabbitMQProperty.getPort(), rabbitMQProperty.getUser(), rabbitMQProperty.getPwd(), rabbitMQProperty.getVhost(), 1)) {
            RabbitMQProperty.Init rabbitMQPropertyInit = rabbitMQProperty.getInit();
            for (Exchange exchange : rabbitMQPropertyInit.getExchanges()) {
                RabbitMqInitializer.exchangeDeclare(connection, exchange);
            }
            for (Queue queue : rabbitMQPropertyInit.getQueues()) {
                RabbitMqInitializer.queueDeclare(connection, queue);
            }
            for (QueueBind queueBind : rabbitMQPropertyInit.getBinds()) {
                RabbitMqInitializer.queueBind(connection, queueBind);
            }
        } catch (IOException e) {
            throw new RabbitMqException(e);
        }
    }

    private static Connection newConnection(RabbitMQProperty rabbitMQProperty) {
        if (rabbitMQProperty.isUseSingleConnection()) {
            return ConnectionFactory.newConnectionFromCache(rabbitMQProperty.getHost(), rabbitMQProperty.getPort(), rabbitMQProperty.getUser(), rabbitMQProperty.getPwd(), rabbitMQProperty.getVhost(), rabbitMQProperty.getConsumer().getParallel());
        }
        return ConnectionFactory.newConnection(rabbitMQProperty.getHost(), rabbitMQProperty.getPort(), rabbitMQProperty.getUser(), rabbitMQProperty.getPwd(), rabbitMQProperty.getVhost(), rabbitMQProperty.getConsumer().getParallel());
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "grus.rabbitmq.consumer.enable", havingValue = "true")
    @ConditionalOnWorkEnv({WorkEnv.DEVELOP, WorkEnv.TEST, WorkEnv.PRODUCT})
    public class ConsumerConfiguration {
        public ConsumerConfiguration() {
            LOGGER.info("rabbitMqConsumer enabled");
        }

        @Bean
        @ConditionalOnMissingBean
        public Connection consumerConnection(RabbitMQProperty rabbitMQProperty) {
            return newConnection(rabbitMQProperty);
        }

        @Bean(initMethod = "init", destroyMethod = "close")
        @ConditionalOnMissingBean
        public RabbitMqConsumer rabbitMqConsumer(RabbitMQProperty rabbitMQProperty, Connection consumerConnection) {
            return new RabbitMqConsumer(consumerConnection, rabbitMQProperty.getConsumer().getParallel(), rabbitMQProperty.getConsumer().getPrefetch(), rabbitMQProperty.getConsumer().getMaxRetry(), rabbitMQProperty.getConsumer().isFallBack());
        }

        @Bean
        @ConditionalOnMissingBean
        public MessageListenerBeanProcessor messageQueueListenerJobBeanProcessor(RabbitMqConsumer rabbitMqConsumer) {
            return new MessageListenerBeanProcessor(rabbitMqConsumer);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "grus.rabbitmq.producer.enable", havingValue = "true")
    @AutoConfigureAfter({ConsumerConfiguration.class})
    public class ProducerConfiguration {
        public ProducerConfiguration() {
            LOGGER.info("rabbitMqProducer enabled");
        }

        @Bean
        @ConditionalOnMissingBean
        public Connection producerConnection(RabbitMQProperty rabbitMQProperty) {
            return newConnection(rabbitMQProperty);
        }

        @Bean
        @ConditionalOnMissingBean
        public RabbitMqProducer rabbitMqProducer(RabbitMQProperty rabbitMQProperty, Connection producerConnection) {
            return new RabbitMqProducer(producerConnection, rabbitMQProperty.getProducer().getParallel(), rabbitMQProperty.getProducer().isConfirm(), rabbitMQProperty.getProducer().isCreateMessageId(), rabbitMQProperty.getProducer().isCreateTimestamp());
        }
    }


}
