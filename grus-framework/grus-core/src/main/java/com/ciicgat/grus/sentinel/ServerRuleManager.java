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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 服务端限流
 * Created by August.Zhou on 2024/4/19 16:59
 */
public class ServerRuleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerRuleManager.class);


    /**
     * 增加资源规则,需要大家根据server端规则来生成resource
     *
     * @param rules
     */
    public static void addRule(String urlPatternSource, boolean regex, Set<RuleOptions<?>> rules) {
        List<DegradeRule> degradeRules = new ArrayList<>();
        List<FlowRule> flowRules = new ArrayList<>();
        for (RuleOptions<?> rule : rules) {
            if (rule instanceof DegradeRuleOptions) {
                DegradeRule sentinelRule = ((DegradeRuleOptions) rule).toSentinelRule(urlPatternSource);
                if (regex) {
                    sentinelRule.setRegex(true);
                }
                degradeRules.add(sentinelRule);
            } else if (rule instanceof FlowRuleOptions) {
                FlowRule sentinelRule = ((FlowRuleOptions) rule).toSentinelRule(urlPatternSource);
                if (regex) {
                    sentinelRule.setRegex(true);
                }
                flowRules.add(sentinelRule);
            }
        }
        if (!degradeRules.isEmpty()) {
            LOGGER.info("addDegradeRule: {}", degradeRules);
            DegradeRuleManager.loadRules(degradeRules);
        }

        if (!flowRules.isEmpty()) {
            LOGGER.info("addFlowRule: {}", flowRules);
            FlowRuleManager.loadRules(flowRules);
        }
    }

    public static void loadSystemRule(int maxThread, int avgRt) {
        LOGGER.info("添加系统规则, maxThread={} ,avgRt={} ms", maxThread, avgRt);
        List<SystemRule> rules = new ArrayList<>();
        SystemRule rule = new SystemRule();
//        由于使用了容器技术，所以cpu和load的统计都有问题。默认关掉它们
        rule.setHighestSystemLoad(-1);
        rule.setHighestCpuUsage(-1);
        rule.setAvgRt(avgRt);
        rule.setMaxThread(maxThread);
        rules.add(rule);
        SystemRuleManager.loadRules(rules);
    }
}
