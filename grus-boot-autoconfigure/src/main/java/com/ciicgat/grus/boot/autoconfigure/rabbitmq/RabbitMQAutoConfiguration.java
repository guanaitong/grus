/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.rabbitmq;

import com.ciicgat.sdk.mq.ConnnectionFactory;
import com.ciicgat.sdk.mq.RabbitMqProducer;
import com.ciicgat.sdk.mq.RabbitMqConsumer;
import com.ciicgat.sdk.mq.Queue;
import com.ciicgat.sdk.mq.QueueBind;
import com.ciicgat.sdk.mq.RabbitMqInitializer;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by August.Zhou on 2022/7/7 12:17
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RabbitMQProperty.class)
@ConditionalOnClass({RabbitMqProducer.class, Connection.class})
public class RabbitMQAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQAutoConfiguration.class);

    private static Connection newConnection(RabbitMQProperty rabbitMQProperty, boolean consumer) {
        return ConnnectionFactory.newConnectionFromCache(rabbitMQProperty.getHost(), rabbitMQProperty.getPort(), rabbitMQProperty.getUser(), rabbitMQProperty.getPwd(), rabbitMQProperty.getVhost(), rabbitMQProperty.isUseSingleConnection() ? true : consumer);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "grus.rabbitmq.init.enable", havingValue = "true")
    public static class InitConfiguration {

        public InitConfiguration(RabbitMQProperty rabbitMQProperty) {
            init(rabbitMQProperty);
        }

        private void init(RabbitMQProperty rabbitMQProperty) {
            Connection connection = newConnection(rabbitMQProperty, true);
            RabbitMQProperty.Init rabbitMQPropertyInit = rabbitMQProperty.getInit();
            for (RabbitMQProperty.Exchange exchange : rabbitMQPropertyInit.getExchanges()) {
                RabbitMqInitializer.exchangeDeclare(connection, exchange.getName(), exchange.getType());
            }
            for (Queue queue : rabbitMQPropertyInit.getQueues()) {
                RabbitMqInitializer.queueDeclare(connection, queue);
            }
            for (QueueBind bind : rabbitMQPropertyInit.getBinds()) {
                RabbitMqInitializer.queueBind(connection, bind.getQueue(), bind.getExchange(), bind.getRoutingKey());
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "grus.rabbitmq.producer.enable", havingValue = "true")
    @AutoConfigureBefore(InitConfiguration.class)
    public static class ProducerConfiguration {

        @Bean
        public Connection producerConnection(RabbitMQProperty rabbitMQProperty) {
            return newConnection(rabbitMQProperty, false);
        }

        @Bean
        public RabbitMqProducer rabbitMqProducer(RabbitMQProperty rabbitMQProperty, Connection producerConnection) {
            return new RabbitMqProducer(producerConnection, rabbitMQProperty.getProducer().getParallel(), rabbitMQProperty.getProducer().isConfirm(), rabbitMQProperty.getProducer().isCreateMessageId(), rabbitMQProperty.getProducer().isCreateTimestamp());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "grus.rabbitmq.consumer.enable", havingValue = "true")
    @AutoConfigureBefore(InitConfiguration.class)
    public static class ConsumerConfiguration {

        @Bean
        public Connection consumerConnection(RabbitMQProperty rabbitMQProperty) {
            return newConnection(rabbitMQProperty, true);
        }

        @Bean(initMethod = "init", destroyMethod = "close")
        public RabbitMqConsumer rabbitMqConsumer(RabbitMQProperty rabbitMQProperty, Connection consumerConnection) {
            return new RabbitMqConsumer(consumerConnection, rabbitMQProperty.getConsumer().getParallel(), rabbitMQProperty.getConsumer().getPrefetch(), rabbitMQProperty.getConsumer().getMaxRetry(), rabbitMQProperty.getConsumer().isFallBack());
        }

        @Bean
        public MessageQueueListenerJobBeanProcessor messageQueueListenerJobBeanProcessor(RabbitMqConsumer rabbitMqConsumer) {
            return new MessageQueueListenerJobBeanProcessor(rabbitMqConsumer);
        }
    }
}
