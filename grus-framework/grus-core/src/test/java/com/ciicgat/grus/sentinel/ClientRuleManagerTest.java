/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by August.Zhou on 2024/4/19 19:58
 */
class ClientRuleManagerTest {

    @Test
    void registerRule1() {
        FlowRuleOptions flowRuleOptions = new FlowRuleOptions();
        flowRuleOptions.setCount(20)
                .setControlBehavior(0)
                .setStrategy(0)
                .setRefResource("")
                .setWarmUpPeriodSec(10)
                .setMaxQueueingTimeMs(10)
                .setGrade(RuleConstant.FLOW_GRADE_THREAD)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        flowRuleOptions.hashCode();

        FlowRuleOptions flowRuleOptions1 = new FlowRuleOptions();
        flowRuleOptions1.setCount(20)
                .setControlBehavior(0)
                .setStrategy(0)
                .setRefResource("")
                .setWarmUpPeriodSec(10)
                .setMaxQueueingTimeMs(10)
                .setGrade(RuleConstant.FLOW_GRADE_THREAD)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        Assertions.assertEquals(flowRuleOptions, flowRuleOptions1);
        Assertions.assertEquals(flowRuleOptions.hashCode(), flowRuleOptions1.hashCode());
        DegradeRuleOptions degradeRule = new DegradeRuleOptions();
        degradeRule.setGrade(0)
                .setCount(200.0d)
                .setTimeWindow(30)
                .setStatIntervalMs(1000)
                .setMinRequestAmount(5)
                .setSlowRatioThreshold(0.5d);

        DegradeRuleOptions degradeRule1 = new DegradeRuleOptions();
        degradeRule1.setGrade(0)
                .setCount(200.0d)
                .setTimeWindow(30)
                .setStatIntervalMs(1000)
                .setMinRequestAmount(5)
                .setSlowRatioThreshold(0.5d);
        Assertions.assertEquals(degradeRule1, degradeRule);
        Assertions.assertEquals(degradeRule1.hashCode(), degradeRule.hashCode());
        ClientRuleManager.registerRule(DegradeService1.class, Set.of(flowRuleOptions), Map.of("testDegradeOnMethod", Set.of(degradeRule)));
    }

    @Test
    void registerRule2() {
        ClientRuleManager.registerRule(DegradeService2.class, new HashSet<>(), new HashMap<>());
    }

    @Test
    void registerRule3() {
        ClientRuleManager.registerRule(DegradeService3.class, new HashSet<>(), new HashMap<>());
    }

    @Test
    void registerRule4() {
        DegradeRuleOptions degradeRule = new DegradeRuleOptions();

        degradeRule.setGrade(0)
                .setCount(200.0d)
                .setTimeWindow(30)
                .setStatIntervalMs(1000)
                .setMinRequestAmount(5)
                .setSlowRatioThreshold(0.5d);
        ClientRuleManager.addAppServiceRule("degrade-test-4", Set.of(degradeRule));
    }
}
