/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.ciicgat.api.core.annotation.ServiceName;
import com.ciicgat.grus.sentinel.annotation.SimpleDegradeRule;
import com.ciicgat.grus.sentinel.annotation.SimpleFlowRule;
import com.ciicgat.grus.service.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.ciicgat.grus.sentinel.RuleContainer.addRule;

/**
 * 客户端限流管理
 *
 * @author August
 * @date 3/15/2024 4:09 PM
 */
public class ClientRuleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRuleManager.class);

    //    优先使用外部传来的RuleOptions，然后使用注解上的RuleOptions
//    具体优先级从高到低：1、外部传来的方法上的RuleOptions 2、方法注解上的RuleOptions 3、外部传来的类上的RuleOptions 4、类注解上的RuleOptions 5、全局的RuleOptions
    public static void registerRule(Class<?> serviceClazz, Set<RuleOptions<?>> classRules, Map<String, Set<RuleOptions<?>>> methodRules) {
        Set<AbstractRule> toAddFlowClassRules = new HashSet<>();
        Set<AbstractRule> toAddDegradeClassRules = new HashSet<>();
        if (classRules != null && !classRules.isEmpty()) {
            ClientRuleManager.addClassRule(serviceClazz, classRules, toAddFlowClassRules, toAddDegradeClassRules);
        }
        if (toAddFlowClassRules.isEmpty()) {
            if (serviceClazz.getAnnotation(SimpleFlowRule.class) != null) {
                FlowRuleOptions flowRuleOptions = toFlowRuleOptions(serviceClazz.getAnnotation(SimpleFlowRule.class));
                ClientRuleManager.addClassRule(serviceClazz, Set.of(flowRuleOptions), toAddFlowClassRules, toAddDegradeClassRules);
            }
        }
        if (toAddDegradeClassRules.isEmpty()) {
            if (serviceClazz.getAnnotation(SimpleDegradeRule.class) != null) {
                DegradeRuleOptions degradeRuleOptions = toDegradeRuleOptions(serviceClazz.getAnnotation(SimpleDegradeRule.class));
                ClientRuleManager.addClassRule(serviceClazz, Set.of(degradeRuleOptions), toAddFlowClassRules, toAddDegradeClassRules);
            }
        }
        Set<AbstractRule> toAddFlowMethodRules = new HashSet<>();
        Set<AbstractRule> toAddDegradeMethodRules = new HashSet<>();
        if (!methodRules.isEmpty()) {
            try {
                for (Map.Entry<String, Set<RuleOptions<?>>> entry : methodRules.entrySet()) {
                    Method method = serviceClazz.getMethod(entry.getKey());
                    ClientRuleManager.addMethodRule(method, entry.getValue(), toAddFlowMethodRules, toAddDegradeMethodRules);
                }
            } catch (NoSuchMethodException e) {
                LOGGER.error("create bean error,serviceName {} class {}", Services.getServiceName(serviceClazz).value(), serviceClazz);
                throw new RuntimeException(e);
            }
        }
        Method[] declaredMethods = serviceClazz.getDeclaredMethods();
        if (toAddFlowMethodRules.isEmpty()) {
            for (Method method : declaredMethods) {
                if (method.isDefault() || methodRules.containsKey(method.getName())) {
                    continue;
                }
                SimpleFlowRule simpleFlowRule = method.getAnnotation(SimpleFlowRule.class);
                if (simpleFlowRule != null) {
                    ClientRuleManager.addMethodRule(method, Set.of(toFlowRuleOptions(simpleFlowRule)), toAddFlowMethodRules, toAddDegradeMethodRules);
                }
            }
        }

        if (toAddDegradeMethodRules.isEmpty()) {
            for (Method method : declaredMethods) {
                if (method.isDefault() || methodRules.containsKey(method.getName())) {
                    continue;
                }
                SimpleDegradeRule simpleDegradeRule = method.getAnnotation(SimpleDegradeRule.class);
                if (simpleDegradeRule != null) {
                    ClientRuleManager.addMethodRule(method, Set.of(toDegradeRuleOptions(simpleDegradeRule)), toAddFlowMethodRules, toAddDegradeMethodRules);
                }
            }
        }
        boolean hasDefined = true;
        if (!toAddFlowMethodRules.isEmpty()) {
            addRule(toAddFlowMethodRules);
        } else if (!toAddFlowClassRules.isEmpty()) {
            addRule(toAddFlowClassRules);
        } else {
            hasDefined = false;
        }
        SentinelDefaultConfig.Flow clientDefaultFlow = SentinelDefaultConfig.CLIENT_DEFAULT_FLOW;
        //无自定义时，增加全局默认流控规则，使用类级别的
        if (!hasDefined && clientDefaultFlow.isEnabled()) {
            FlowRuleOptions flowRuleOptions = new FlowRuleOptions();
            flowRuleOptions.setCount(clientDefaultFlow.getCount())
                    .setGrade(clientDefaultFlow.getGrade())
                    .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
            ClientRuleManager.addClassRule(serviceClazz, Set.of(flowRuleOptions), toAddFlowClassRules, toAddDegradeClassRules);
            addRule(toAddFlowClassRules);
        }
        hasDefined = true;
        if (!toAddDegradeMethodRules.isEmpty()) {
            addRule(toAddDegradeMethodRules);
        } else if (!toAddDegradeClassRules.isEmpty()) {
            addRule(toAddDegradeClassRules);
        } else {
            hasDefined = false;
        }
        SentinelDefaultConfig.Degrade clientDefaultDegrade = SentinelDefaultConfig.CLIENT_DEFAULT_DEGRADE;
        //无自定义时，增加全局默认降级规则
        if (!hasDefined && clientDefaultDegrade.isEnabled()) {
            DegradeRuleOptions degradeRuleOptions = new DegradeRuleOptions();
            degradeRuleOptions.setGrade(clientDefaultDegrade.getGrade())
                    .setCount(clientDefaultDegrade.getCount())
                    .setTimeWindow(clientDefaultDegrade.getTimeWindow())
                    .setStatIntervalMs(1000)
                    .setMinRequestAmount(clientDefaultDegrade.getMinRequestAmount())
                    .setSlowRatioThreshold(clientDefaultDegrade.getSlowRatioThreshold());
            ClientRuleManager.addClassRule(serviceClazz, Set.of(degradeRuleOptions), toAddFlowClassRules, toAddDegradeClassRules);
            addRule(toAddDegradeClassRules);
        }

    }

    private static FlowRuleOptions toFlowRuleOptions(SimpleFlowRule simpleFlowRule) {
        FlowRuleOptions flowRuleOptions = new FlowRuleOptions();
        flowRuleOptions.setCount(simpleFlowRule.value())
                .setGrade(simpleFlowRule.grade())
                .setControlBehavior(simpleFlowRule.controlBehavior());
        return flowRuleOptions;
    }

    private static DegradeRuleOptions toDegradeRuleOptions(SimpleDegradeRule simpleDegradeRule) {
        DegradeRuleOptions degradeRuleOptions = new DegradeRuleOptions();
        degradeRuleOptions.setGrade(simpleDegradeRule.grade())
                .setCount(simpleDegradeRule.value())
                .setTimeWindow(simpleDegradeRule.timeWindow())
                .setStatIntervalMs(simpleDegradeRule.statIntervalMs())
                .setMinRequestAmount(simpleDegradeRule.minRequestAmount())
                .setSlowRatioThreshold(simpleDegradeRule.slowRatioThreshold());
        return degradeRuleOptions;
    }

    /**
     * 增加整个应用服务的限流规则。应用下所有的接口都生效
     *
     * @param service
     */
    public static void addAppServiceRule(String service, Set<RuleOptions<?>> rules) {
        String resource = Resources.buildAppServiceResource(service);
        Set<AbstractRule> abstractRules = new HashSet<>();
        for (RuleOptions<?> ruleOptions : rules) {
            AbstractRule sentinelRule = ruleOptions.toSentinelRule(resource);
            sentinelRule.setRegex(true);
            abstractRules.add(sentinelRule);
        }
        addRule(abstractRules);
    }

    /**
     * 增加service的rule
     *
     * @param apiClass
     * @param rules
     */
    private static void addClassRule(Class<?> apiClass, Set<RuleOptions<?>> rules, Set<AbstractRule> toAddFlowClassRules, Set<AbstractRule> toAddDegradeClassRules) {
        ServiceName serviceName = Services.getServiceName(apiClass);
        String resource = Resources.buildClassResource(serviceName.value(), apiClass.getSimpleName());
        for (RuleOptions<?> ruleOptions : rules) {
            AbstractRule sentinelRule = ruleOptions.toSentinelRule(resource);
            sentinelRule.setRegex(true);
            if (sentinelRule instanceof com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule) {
                toAddDegradeClassRules.add(sentinelRule);
            } else if (sentinelRule instanceof com.alibaba.csp.sentinel.slots.block.flow.FlowRule) {
                toAddFlowClassRules.add(sentinelRule);
            }
        }
    }

    /**
     * 增加某个接口方法的rule
     *
     * @param method
     * @param rules
     */
    private static void addMethodRule(Method method, Set<RuleOptions<?>> rules, Set<AbstractRule> toAddFlowMethodRules, Set<AbstractRule> toAddDegradeMethodRules) {
        Class<?> apiClass = method.getDeclaringClass();
        ServiceName serviceName = Services.getServiceName(apiClass);
        String resource = Resources.buildMethodResource(serviceName.value(), method);
        for (RuleOptions<?> ruleOptions : rules) {
            AbstractRule sentinelRule = ruleOptions.toSentinelRule(resource);
            if (sentinelRule instanceof com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule) {
                toAddDegradeMethodRules.add(sentinelRule);
            } else if (sentinelRule instanceof com.alibaba.csp.sentinel.slots.block.flow.FlowRule) {
                toAddFlowMethodRules.add(sentinelRule);
            }
        }
    }


}
