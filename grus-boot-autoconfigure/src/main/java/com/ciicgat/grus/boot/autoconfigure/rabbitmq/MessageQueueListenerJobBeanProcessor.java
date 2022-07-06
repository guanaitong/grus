/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.rabbitmq;

import com.ciicgat.sdk.mq.MsgProcessor;
import com.ciicgat.sdk.mq.RabbitMqConsumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created by August.Zhou on 2022/7/7 16:17
 */
public class MessageQueueListenerJobBeanProcessor implements BeanPostProcessor {
    private final RabbitMqConsumer rabbitMqConsumer;

    public MessageQueueListenerJobBeanProcessor(RabbitMqConsumer rabbitMqConsumer) {
        this.rabbitMqConsumer = rabbitMqConsumer;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        MessageQueueListener messageQueueListener = bean.getClass().getAnnotation(MessageQueueListener.class);
        if (messageQueueListener == null) {
            return bean;
        }
        if (!(bean instanceof MsgProcessor)) {
            throw new RuntimeException("bean class " + bean.getClass().getName() + " should implements com.ciicgat.sdk.mq.MsgProcessor");
        }
        rabbitMqConsumer.consume(messageQueueListener.value(), (MsgProcessor) bean);
        return bean;
    }


}
