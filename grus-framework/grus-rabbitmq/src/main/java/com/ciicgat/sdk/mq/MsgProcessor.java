/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;


/**
 * 消息处理器。
 * 因为一个exchange只有一个MsgReceiver，而一个MsgReceiver只有一个MsgProcessor。
 * 所以一个MsgProcessor会处理一个exchange绑定的所有队列，通过routingKey区分队列。
 * 为了便捷，可使用上层的SingleRouteProcessor或者MultiRouteProcessor
 * <p>
 * Created by cpx on 2017-08-07.
 */
@FunctionalInterface
public interface MsgProcessor {
    /**
     * 该方法是多线程调用，需要保证线程安全。
     * 上层实现时，需要根据情况捕获异常、返回对应的状态。
     * 如果上层没有捕获异常，则默认采用重试机制。重试次数可以自定义，默认5次。
     *
     * @param msg        消息内容。为了扩展性，换成了对象
     * @return
     */
    State apply(Msg msg);

}
