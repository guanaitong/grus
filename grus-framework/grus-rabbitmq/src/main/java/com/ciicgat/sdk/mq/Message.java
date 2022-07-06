/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;

/**
 * Created by August.Zhou on 2022/7/6 09:40
 *
 * @param msgId      消息id，发送时产生的。可用于消费时的幂等逻辑。如果业务已经有用于幂等的字段，可以忽略。老版本msgId为null
 * @param routingKey 交换机
 * @param routingKey 路由
 * @param queue      队列
 * @param body       消息内容
 */
public record Message(String msgId, String exchange, String routingKey, String queue, String body) {

}
