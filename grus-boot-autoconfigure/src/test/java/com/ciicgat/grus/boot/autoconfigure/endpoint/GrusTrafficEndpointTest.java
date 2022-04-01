/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.endpoint;

import com.ciicgat.sdk.servlet.TrafficCounter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class GrusTrafficEndpointTest {
    @Test
    void test() {
        GrusTrafficEndpoint grusTrafficEndpoint = new GrusTrafficEndpoint();
        TrafficCounter.trafficIncrement();
        Map<String, Long> trafficMap = grusTrafficEndpoint.traffic();
        Assertions.assertTrue(trafficMap.size() > 0);
    }
}
