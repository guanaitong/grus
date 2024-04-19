/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;

public class DegradeRuleOptions implements RuleOptions<DegradeRule> {

    /**
     * 熔断策略(0: average RT, 1: exception ratio, 2: exception count).默认0
     */
    private int grade;

    /**
     * 慢调用比例模式下为慢调用临界 RT（超出该值计为慢调用）；异常比例/异常数模式下为对应的阈值
     * In average RT mode, it means the maximum response time(RT) in milliseconds.
     * In exception ratio mode, it means exception ratio which between 0.0 and 1.0.
     * In exception count mode, it means exception count
     */
    private double count;

    /**
     * 熔断时长，单位为 s
     */
    private int timeWindow = 30;

    /**
     * 统计时长（单位为 ms）
     */
    private int statIntervalMs = 100;

    /**
     * 熔断触发的最小请求数
     */
    private int minRequestAmount = 5;

    /**
     * 慢调用比例阈值，仅慢调用比例模式有效
     */
    private double slowRatioThreshold = 0.5;

    public DegradeRuleOptions setGrade(int grade) {
        this.grade = grade;
        return this;
    }

    public DegradeRuleOptions setCount(double count) {
        this.count = count;
        return this;
    }

    public DegradeRuleOptions setTimeWindow(int timeWindow) {
        this.timeWindow = timeWindow;
        return this;
    }

    public DegradeRuleOptions setStatIntervalMs(int statIntervalMs) {
        this.statIntervalMs = statIntervalMs;
        return this;
    }

    public DegradeRuleOptions setMinRequestAmount(int minRequestAmount) {
        this.minRequestAmount = minRequestAmount;
        return this;
    }

    public DegradeRuleOptions setSlowRatioThreshold(double slowRatioThreshold) {
        this.slowRatioThreshold = slowRatioThreshold;
        return this;
    }

    public int getGrade() {
        return grade;
    }

    public double getCount() {
        return count;
    }

    public int getTimeWindow() {
        return timeWindow;
    }

    public int getStatIntervalMs() {
        return statIntervalMs;
    }

    public int getMinRequestAmount() {
        return minRequestAmount;
    }

    public double getSlowRatioThreshold() {
        return slowRatioThreshold;
    }

    @Override
    public String toString() {
        return "DegradeRuleOptions{" +
                "grade=" + grade +
                ", count=" + count +
                ", timeWindow=" + timeWindow +
                ", statIntervalMs=" + statIntervalMs +
                ", minRequestAmount=" + minRequestAmount +
                ", slowRatioThreshold=" + slowRatioThreshold +
                '}';
    }

    @Override
    public DegradeRule toSentinelRule(String resource) {
        DegradeRule degradeRule = new DegradeRule(resource);
        degradeRule.setGrade(this.getGrade())
                .setCount(this.getCount())
                .setTimeWindow(this.getTimeWindow())
                .setMinRequestAmount(this.getMinRequestAmount())
                .setSlowRatioThreshold(this.getSlowRatioThreshold())
                .setStatIntervalMs(this.getStatIntervalMs());
        return degradeRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DegradeRuleOptions that = (DegradeRuleOptions) o;

        if (grade != that.grade) return false;
        if (Double.compare(count, that.count) != 0) return false;
        if (timeWindow != that.timeWindow) return false;
        if (statIntervalMs != that.statIntervalMs) return false;
        if (minRequestAmount != that.minRequestAmount) return false;
        return Double.compare(slowRatioThreshold, that.slowRatioThreshold) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = grade;
        temp = Double.doubleToLongBits(count);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + timeWindow;
        result = 31 * result + statIntervalMs;
        result = 31 * result + minRequestAmount;
        temp = Double.doubleToLongBits(slowRatioThreshold);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
