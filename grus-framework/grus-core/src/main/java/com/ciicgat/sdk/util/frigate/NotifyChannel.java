/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

/**
 * Created by Albert on 2018/10/29.
 */
public enum NotifyChannel {

    /**
     * 站内信
     */
    DEFAULT(0),
    QY_WE_CHAT(1),
    EMAIL(2),
    SMS(3),
    QY_WE_CHAT_AND_EMAIL(4),
    QY_WE_CHAT_AND_SMS(5),
    EMAIL_AND_SMS(6),
    /**
     * 全渠道发送 ： 微信+邮件+短信
     */
    ALL(7);

    final int code;

    NotifyChannel(int code) {
        this.code = code;
    }

    public static NotifyChannel valueOf(int code) {
        for (NotifyChannel value : NotifyChannel.values()) {
            if (value.code() == code) {
                return value;
            }
        }
        return null;
    }

    public int code() {
        return code;
    }

}
