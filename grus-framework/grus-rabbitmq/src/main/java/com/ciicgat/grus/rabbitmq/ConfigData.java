/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.rabbitmq;

import com.ciicgat.grus.json.JSON;

/**
 * 应用使用的mq的配置数据
 * Created by August.Zhou on 2022/7/2 22:03
 */
public record ConfigData(String appName, String exchange, String routingKey, String queue, boolean consumer) {
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
