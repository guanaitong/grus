/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

/**
 * Created by August.Zhou on 2022/7/2 10:25
 */
public enum State {
    /**
     * 处理成功。消息被确认，从队列里删除。
     */
    OK,
    /**
     * 处理失败，重试，重新进队列重试
     */
    Retry,
    /**
     * 处理失败，丢弃。如果设置了死信队列，则会进入死信队列
     */
    Abort
}
