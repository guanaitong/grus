/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;

import java.util.Objects;

/**
 * Created by August.Zhou on 2024/3/15 22:37
 */
public class FlowRuleOptions implements RuleOptions<FlowRule> {
    /**
     * The threshold type of flow control (0: thread count, 1: QPS).
     */
    private int grade = RuleConstant.FLOW_GRADE_THREAD;

    /**
     * Flow control threshold count.
     * 如果grade为qps，那么为resource能成熟的最大qps
     * 如果grade为thread，那么为resource能成熟的最大并发
     */
    private double count;

    /**
     * Flow control strategy based on invocation chain.
     * <p>
     * {@link RuleConstant#STRATEGY_DIRECT} for direct flow control (by origin);
     * {@link RuleConstant#STRATEGY_RELATE} for relevant flow control (with relevant resource);
     * {@link RuleConstant#STRATEGY_CHAIN} for chain flow control (by entrance resource).
     */
    private int strategy = RuleConstant.STRATEGY_DIRECT;

    /**
     * Reference resource in flow control with relevant resource or context.
     */
    private String refResource;

    /**
     * Rate limiter control behavior.
     * 0. default(reject directly), 1. warm up, 2. rate limiter, 3. warm up + rate limiter
     */
    private int controlBehavior = RuleConstant.CONTROL_BEHAVIOR_DEFAULT;

    private int warmUpPeriodSec = 10;

    /**
     * Max queueing time in rate limiter behavior.
     */
    private int maxQueueingTimeMs = 500;

    public int getGrade() {
        return grade;
    }

    public FlowRuleOptions setGrade(int grade) {
        this.grade = grade;
        return this;
    }

    public double getCount() {
        return count;
    }

    public FlowRuleOptions setCount(double count) {
        this.count = count;
        return this;
    }

    public int getStrategy() {
        return strategy;
    }

    public FlowRuleOptions setStrategy(int strategy) {
        this.strategy = strategy;
        return this;
    }

    public String getRefResource() {
        return refResource;
    }

    public FlowRuleOptions setRefResource(String refResource) {
        this.refResource = refResource;
        return this;
    }

    public int getControlBehavior() {
        return controlBehavior;
    }

    public FlowRuleOptions setControlBehavior(int controlBehavior) {
        this.controlBehavior = controlBehavior;
        return this;
    }

    public int getWarmUpPeriodSec() {
        return warmUpPeriodSec;
    }

    public FlowRuleOptions setWarmUpPeriodSec(int warmUpPeriodSec) {
        this.warmUpPeriodSec = warmUpPeriodSec;
        return this;
    }

    public int getMaxQueueingTimeMs() {
        return maxQueueingTimeMs;
    }

    public FlowRuleOptions setMaxQueueingTimeMs(int maxQueueingTimeMs) {
        this.maxQueueingTimeMs = maxQueueingTimeMs;
        return this;
    }

    @Override
    public FlowRule toSentinelRule(String resource) {
        FlowRule flowRule = new FlowRule(resource);
        flowRule.setGrade(this.grade)
                .setCount(this.count)
                .setStrategy(this.strategy)
                .setRefResource(this.refResource)
                .setControlBehavior(this.controlBehavior)
                .setWarmUpPeriodSec(this.warmUpPeriodSec)
                .setMaxQueueingTimeMs(this.maxQueueingTimeMs)
                .setClusterMode(false);
        return flowRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowRuleOptions that = (FlowRuleOptions) o;

        if (grade != that.grade) return false;
        if (Double.compare(count, that.count) != 0) return false;
        if (strategy != that.strategy) return false;
        if (controlBehavior != that.controlBehavior) return false;
        if (warmUpPeriodSec != that.warmUpPeriodSec) return false;
        if (maxQueueingTimeMs != that.maxQueueingTimeMs) return false;
        return Objects.equals(refResource, that.refResource);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = grade;
        temp = Double.doubleToLongBits(count);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + strategy;
        result = 31 * result + (refResource != null ? refResource.hashCode() : 0);
        result = 31 * result + controlBehavior;
        result = 31 * result + warmUpPeriodSec;
        result = 31 * result + maxQueueingTimeMs;
        return result;
    }
}
