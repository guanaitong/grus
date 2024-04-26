/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.ciicgat.sdk.lang.threads.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * 由于FlowRuleManager.loadRules和DegradeRuleManager.loadRules全局只能调用一次（后面的调用会覆盖前面的）
 * 所以，只能使用一个临时的容器将规则都存起来，然后延迟加载
 * Created by August.Zhou on 2024/4/26 20:58
 */
public class RuleContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleContainer.class);

    private static final List<DegradeRule> degradeRules = new Vector<>();
    private static final List<FlowRule> flowRules = new Vector<>();

    static {
        Threads.newDaemonThread(() -> {
//                应用程序启动5分钟后，才加载规则
            Threads.sleepSeconds(300);
            loadRules();
        }, "rule-loader", Thread.MIN_PRIORITY).start();
    }

    public static void loadRules() {
        LOGGER.info("load all rules, flowRules size {}, degradeRules size {}", flowRules.size(), degradeRules.size());
        FlowRuleManager.loadRules(flowRules);
        DegradeRuleManager.loadRules(degradeRules);
    }

    public static void addRule(Set<AbstractRule> rules) {
        for (AbstractRule rule : rules) {
            LOGGER.info("add rule {}", rule);
            if (rule instanceof FlowRule) {
                flowRules.add((FlowRule) rule);
            } else if (rule instanceof DegradeRule) {
                degradeRules.add((DegradeRule) rule);
            }
        }
    }
}
