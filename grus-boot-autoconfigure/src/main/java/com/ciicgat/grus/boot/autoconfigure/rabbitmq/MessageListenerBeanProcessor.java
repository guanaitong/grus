/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.rabbitmq;

import com.ciicgat.grus.rabbitmq.RabbitMqConsumer;
import com.ciicgat.grus.rabbitmq.processor.MsgProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created by August.Zhou on 2022/7/7 16:17
 */
public class MessageListenerBeanProcessor implements BeanPostProcessor {
    private final RabbitMqConsumer rabbitMqConsumer;

    public MessageListenerBeanProcessor(RabbitMqConsumer rabbitMqConsumer) {
        this.rabbitMqConsumer = rabbitMqConsumer;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        MessageListener messageListener = bean.getClass().getAnnotation(MessageListener.class);
        if (messageListener == null) {
            return bean;
        }
        if (!(bean instanceof MsgProcessor)) {
            throw new RuntimeException("bean class " + bean.getClass().getName() + " should implements com.ciicgat.sdk.mq.MsgProcessor");
        }
        rabbitMqConsumer.consume(messageListener.value(), (MsgProcessor) bean);
        return bean;
    }


}
