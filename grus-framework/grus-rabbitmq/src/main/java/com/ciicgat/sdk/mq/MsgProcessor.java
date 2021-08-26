/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.mq;


/**
 * 消息处理器
 * Created by cpx on 2017-08-07.
 */
@FunctionalInterface
public interface MsgProcessor {

    /**
     * 该方法是多线程调用，需要保证线程安全。返回true表示消息处理成功，框架自动做ack。返回false，不会做ack。
     * 发生RuntimeException时，框架也不会ack
     *
     * @param msg
     * @return
     */

    boolean apply(String routingKey, String msg);


}
