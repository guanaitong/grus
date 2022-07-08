/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.ciicgat.sdk.util.system.Systems;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.ciicgat.grus.rabbitmq.MqConstants.GLOBAL_CONFIG_EXCHANGE_NAME;

/**
 * Created by August.Zhou on 2022/7/7 14:22
 */
public class RabbitMqInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqInitializer.class);

    public static void exchangeDeclare(Connection connection, String exchange, BuiltinExchangeType exchangeType) {
        execute(connection, channel -> {
            channel.exchangeDeclare(exchange, exchangeType, true);
            addConfigData(exchange, null, "", false, channel);
        });
        LOGGER.info("exchange[{}] type[{}] declared", exchange, exchangeType);
    }

    public static void queueDeclareAndBind(Connection connection, Queue queue, String exchange, String routingKey) {
        queueDeclare(connection, queue);
        queueBind(connection, queue.getName(), exchange, routingKey);
    }

    public static void queueDeclare(Connection connection, Queue queue) {
        String name = queue.getName();
        try {
            execute(connection, channel -> channel.queueDeclarePassive(name));
        } catch (RabbitMqException e) {
            LOGGER.warn("queue[{}] not found", name);
            execute(connection, channel -> channel.queueDeclare(name, queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(), getFinalArgs(queue)));
        }
        LOGGER.info("queue[{}] declared", name);
    }

    public static void queueBind(Connection connection, String queue, String exchange, String routingKey) {
        execute(connection, channel -> {
            channel.queueBind(queue, exchange, routingKey);
            addConfigData(exchange, routingKey, queue, true, channel);
        });
        LOGGER.info("queue[{}] exchange[{}] routingKey[{}] bound", queue, exchange, routingKey);
    }

    public static void addConfigData(String exchange, String routingKey, String queue, boolean consumer, Channel channel) throws IOException {
        ConfigData configData = new ConfigData(Systems.APP_NAME, exchange, routingKey, queue, consumer);
        channel.basicPublish(GLOBAL_CONFIG_EXCHANGE_NAME, "", false, MessageProperties.PERSISTENT_BASIC, configData.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static void execute(Connection connection, channelAction channelAction) {
        try (Channel channel = connection.createChannel()) {
            channelAction.apply(channel);
        } catch (IOException e) {
            throw new RabbitMqException(e);
        } catch (TimeoutException e) {
            throw new RabbitMqException(e);
        }
    }

    private static Map<String, Object> getFinalArgs(Queue queue) {
        Map<String, Object> args = new HashMap<>(queue.getArgs());
        if (!args.containsKey(MqConstants.EXPIRE_KEY)) {
            args.put(MqConstants.EXPIRE_KEY, MqConstants.DEFAULT_TTL);
        }
        if (!args.containsKey(MqConstants.MESSAGE_TTL_KEY)) {
            args.put(MqConstants.MESSAGE_TTL_KEY, MqConstants.DEFAULT_TTL);
        }
        // 队列上写上应用名字，方便排查
        args.put("x-app-name", Systems.APP_NAME);
        return Collections.unmodifiableMap(args);
    }

    interface channelAction {
        void apply(Channel channel) throws IOException;
    }
}
