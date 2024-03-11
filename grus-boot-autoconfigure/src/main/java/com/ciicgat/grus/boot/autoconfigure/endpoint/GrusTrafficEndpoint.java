/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.endpoint;

import com.ciicgat.sdk.servlet.TrafficCounter;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Map;

/**
 * 应用接口调用次数监控端点
 *
 * @author andy
 */
@Endpoint(id = "traffic")
public class GrusTrafficEndpoint {

    //表示读操作 用GET请求可以获取到
    @ReadOperation
    public Map<String, Long> traffic() {
        return TrafficCounter.getCounter();
    }

}
