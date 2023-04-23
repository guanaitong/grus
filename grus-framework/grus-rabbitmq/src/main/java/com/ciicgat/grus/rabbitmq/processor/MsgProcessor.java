/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq.processor;


import com.ciicgat.grus.rabbitmq.Message;
import com.ciicgat.grus.rabbitmq.State;

/**
 * 消息处理器。
 * 一般一个queue会有一个MsgProcessor
 * 为了便捷，可使用上层的JSONObjectProcessor
 * <p>
 * Created by August.Zhou on 2022/7/8 21:22
 */
public interface MsgProcessor {

    /**
     * 该方法是多线程调用，需要保证线程安全。
     * 上层实现时，需要根据情况捕获异常、返回对应的状态。
     * 如果上层没有捕获异常，则默认采用重试机制。重试次数可以自定义，默认5次。
     *
     * @param message 消息内容。为了扩展性，换成了对象
     * @return
     */
    State onMessage(Message message);

}
