/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.frigate;

/**
 * @author August.Zhou
 * @date 2019-03-25 17:44
 */
public interface FrigateMessageConstants {

    /**
     * 获取警报系统的根地址
     *
     * @return 根地址
     */
    default String getBaseUrl() {
        return "http://127.0.0.1:8087";
    }

    default String getByQiWeiXinUrl() {
        return getBaseUrl() + "/v2/message/sendMsgByWeChatIds";
    }

    default String getByGroupUrl() {
        return getBaseUrl() + "/v2/message/sendMsgByGroups";
    }

    default String getByAppNameUrl() {
        return getBaseUrl() + "/v2/message/sendMsgByAppNames";
    }

}
