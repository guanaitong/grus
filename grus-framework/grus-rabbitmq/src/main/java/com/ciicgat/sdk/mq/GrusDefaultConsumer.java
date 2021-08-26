/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

import com.ciicgat.grus.core.Module;
import com.ciicgat.sdk.util.frigate.FrigateNotifier;
import com.ciicgat.sdk.util.frigate.NotifyChannel;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by August.Zhou on 2019-04-25 19:12.
 */
public class GrusDefaultConsumer extends DefaultConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrusDefaultConsumer.class);

    private MsgProcessor msgProcessor;
    private final AtomicBoolean isRunning;

    GrusDefaultConsumer(Channel channel, MsgProcessor msgProcessor, AtomicBoolean isRunning) {
        super(channel);
        this.msgProcessor = msgProcessor;
        this.isRunning = Objects.requireNonNull(isRunning);
    }

    @Override
    public final void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        if (isRunning.get()) {
            handleDelivery0(consumerTag, envelope, properties, body);
        } else {
            getChannel().basicReject(envelope.getDeliveryTag(), true);
            // 下面的close可能导致死循环
//            CloseUtils.close(getChannel());
        }
    }

    protected void handleDelivery0(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String text;
        try {
            text = new String(body, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("UnsupportedEncodingException", e);
            getChannel().basicAck(envelope.getDeliveryTag(), false);
            return;
        }

        try {
            boolean b = msgProcessor.apply(envelope.getRoutingKey(), text);
            if (b) {
                getChannel().basicAck(envelope.getDeliveryTag(), false);
            } else {
                // 消息处理失败，重新入队
                getChannel().basicReject(envelope.getDeliveryTag(), true);
                String msg = msgProcessor.getClass().getName() + "处理失败(将重新入队)[需开发紧急处理]:" + text;
                FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT_AND_EMAIL, Module.RABBITMQ, msg, null);
                LOGGER.warn(msg);
            }
        } catch (Exception e) {
            FrigateNotifier.sendMessageByAppName(NotifyChannel.QY_WE_CHAT_AND_EMAIL, Module.RABBITMQ, "rabbitmq error", e);
            LOGGER.error("msg:" + text, e);
            throw e;
        }
    }
}
