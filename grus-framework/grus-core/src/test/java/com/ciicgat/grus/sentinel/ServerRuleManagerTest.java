/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Created by August.Zhou on 2024/4/19 20:15
 */
class ServerRuleManagerTest {

    @Test
    void addRule() {
        FlowRuleOptions flowRuleOptions = new FlowRuleOptions();
        flowRuleOptions.setCount(20)
                .setControlBehavior(0)
                .setStrategy(0)
                .setRefResource("")
                .setWarmUpPeriodSec(10)
                .setMaxQueueingTimeMs(10)
                .setGrade(RuleConstant.FLOW_GRADE_THREAD)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        ServerRuleManager.addRule("/user/get", false, Set.of(flowRuleOptions));
        ServerRuleManager.addRule("/user/get", true, Set.of(flowRuleOptions));
    }

    @Test
    void loadSystemRule() {
        ServerRuleManager.loadSystemRule(100, 2000);
    }
}
