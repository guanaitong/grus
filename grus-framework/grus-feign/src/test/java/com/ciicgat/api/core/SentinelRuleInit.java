/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.ciicgat.grus.sentinel.RuleContainer;

import java.util.Set;

/**
 * Created by August.Zhou on 2024/4/26 22:57
 */
public class SentinelRuleInit {

    static {
        init();
    }

    public static void init() {
        FlowRule fallBackFlowRule = new FlowRule("fallback.FallbackService.getWithOutFallback");
        fallBackFlowRule.setCount(0);
        fallBackFlowRule.setGrade(0);

        FlowRule flowTestRule1 = new FlowRule("flow-test.FlowService.testMethodOfQps");
        flowTestRule1.setCount(20);
        flowTestRule1.setGrade(RuleConstant.FLOW_GRADE_QPS);

        FlowRule flowTestRule2 = new FlowRule("flow-test.FlowService.testMethodOfConcurrency");
        flowTestRule2.setCount(5);
        flowTestRule2.setGrade(RuleConstant.FLOW_GRADE_THREAD);

        DegradeRule degradeRule1 = new DegradeRule("degrade-test.DegradeService.testDegradeOnMethod");
        degradeRule1.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType())
                .setCount(200.0d)
                .setTimeWindow(10)
                .setMinRequestAmount(5)
                .setStatIntervalMs(1000)
                .setSlowRatioThreshold(0.5d);

        DegradeRule degradeRule2 = new DegradeRule("degrade-test.DegradeService.*");
        degradeRule2.setRegex(true);
        degradeRule2.setCount(200.0d)
                .setTimeWindow(10)
                .setMinRequestAmount(5)
                .setStatIntervalMs(1000)
                .setSlowRatioThreshold(0.5d);


        RuleContainer.addRule(Set.of(fallBackFlowRule, flowTestRule1, flowTestRule2, degradeRule1, degradeRule2));

        RuleContainer.loadRules();
    }

    public static void doSth() {

    }
}
