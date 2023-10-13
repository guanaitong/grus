/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.rabbitmq;

import com.ciicgat.grus.rabbitmq.RabbitMqConsumer;
import com.ciicgat.grus.rabbitmq.processor.MsgProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

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
        MessageListener messageListener = AnnotationUtils.findAnnotation(bean.getClass(), MessageListener.class);
        if (messageListener == null) {
            return bean;
        }
        if (!(bean instanceof MsgProcessor)) {
            throw new RuntimeException("bean class " + bean.getClass().getName() + " should implements com.ciicgat.sdk.mq.MsgProcessor");
        }
        String[] queues = messageListener.value();
        for (String queue : queues) {
            rabbitMqConsumer.consume(queue, (MsgProcessor) bean);
        }
        return bean;
    }


}
