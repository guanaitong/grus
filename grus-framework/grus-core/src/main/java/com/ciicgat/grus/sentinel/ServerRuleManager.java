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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * 服务端限流
 * Created by August.Zhou on 2024/4/19 16:59
 */
public class ServerRuleManager {

    /**
     * 对controller下，所有的方法（不包含非requestMapping）增加规则
     *
     * @param withMethod，默认否
     * @param controllerClazz
     * @param rules
     */
    public static void addClassRule(boolean withMethod, Class<?> controllerClazz, Set<RuleOptions<?>> rules) {
        RequestMapping requestMapping = findMergedAnnotation(controllerClazz,
                RequestMapping.class);
        if (requestMapping == null || requestMapping.value().length == 0) {
            return;
        }
        if (withMethod) {
            for (String path : requestMapping.value()) {
                for (RequestMethod requestMethod : requestMapping.method()) {
                    addRule(requestMethod + ":" + path, rules);
                }
            }
        } else {
            for (String path : requestMapping.value()) {
                addRule(path, rules);
            }
        }
    }

    /**
     * 对controller的某一个方法增加规则，方法无requestMapping修饰是无用
     *
     * @param controllerClazz
     * @param method
     * @param rules
     */
    public static void addMethodRule(boolean withMethod, Class<?> controllerClazz, String method, Set<RuleOptions<?>> rules) {
        Optional<Method> any = Arrays.stream(controllerClazz.getDeclaredMethods()).filter(m -> method.equals(m.getName())).findAny();
        any.ifPresent(m -> {
            RequestMapping requestMapping = findMergedAnnotation(m,
                    RequestMapping.class);
            if (requestMapping == null || requestMapping.value().length == 0) {
                return;
            }
            if (withMethod) {
                for (String path : requestMapping.value()) {
                    for (RequestMethod requestMethod : requestMapping.method()) {
                        addRule(requestMethod + ":" + path, rules);
                    }
                }
            } else {
                for (String path : requestMapping.value()) {
                    addRule(path, rules);
                }
            }
        });
    }

    /**
     * 增加资源规则
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
