/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

/**
 * Created by August.Zhou on 2022/7/2 20:41
 */
public interface MqConstants {
    int DEFAULT_TTL = 7 * 24 * 60 * 60 * 1000;

    /**
     * 队列的ttl时间，超过此时间，队列会自动删除
     */
    String EXPIRE_KEY = "x-expires";
    /**
     * 消息的ttl时间，超过此时间，消息会自动删除
     */
    String MESSAGE_TTL_KEY = "x-message-ttl";
    /**
     * 死信队列的交换机
     */
    String DEAD_LETTER_EXCHANGE_KEY = "x-dead-letter-exchange";
    /**
     * 死信队列的路由key
     */
    String DEAD_LETTER_ROUTING_KEY_KEY = "x-dead-letter-routing-key";

    /**
     * 全局的死信队列名字
     */
    String GLOBAL_DEAD_EXCHANGE_NAME = "global.dead.exchange";

    /**
     * 消息id
     */
    String MSG_ID_KEY = "x-msg-id";

    /**
     * 全局的配置队列
     */
    String GLOBAL_CONFIG_EXCHANGE_NAME = "global.config.exchange";
}
