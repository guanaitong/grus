/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 服务端限流
 * Created by August.Zhou on 2024/4/19 16:59
 */
public class ServerRuleManager {

    /**
     * 增加资源规则,需要大家根据server端规则来生成resource
     *
     * @param rules
     */
    public static void addRule(String urlPatternSource, Set<RuleOptions<?>> rules) {
        List<DegradeRule> degradeRules = new ArrayList<>();
        List<FlowRule> flowRules = new ArrayList<>();
        for (RuleOptions<?> rule : rules) {
            if (rule instanceof DegradeRuleOptions) {
                degradeRules.add(((DegradeRuleOptions) rule).toSentinelRule(urlPatternSource));
            } else if (rule instanceof FlowRuleOptions) {
                flowRules.add(((FlowRuleOptions) rule).toSentinelRule(urlPatternSource));
            }
        }
        if (!degradeRules.isEmpty()) {
            DegradeRuleManager.loadRules(degradeRules);
        }

        if (!flowRules.isEmpty()) {
            FlowRuleManager.loadRules(flowRules);
        }
    }

    public static void loadSystemRule(int maxThread, int avgRt) {
        List<SystemRule> rules = new ArrayList<>();
        SystemRule rule = new SystemRule();
        rule.setHighestSystemLoad(-1);
        rule.setHighestCpuUsage(-1);
        rule.setAvgRt(avgRt);
        rule.setMaxThread(maxThread);
        rules.add(rule);
        SystemRuleManager.loadRules(rules);
    }
}
