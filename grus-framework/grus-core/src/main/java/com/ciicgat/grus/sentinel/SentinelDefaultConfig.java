/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;

/**
 * Created by August.Zhou on 2022/10/20 17:49
 */
public class SentinelDefaultConfig {
    public static final String SENTINEL_JOB_CONTEXT_NAME = "sentinel_job_context";
    public static final String SENTINEL_CONSUMER_CONTEXT_NAME = "sentinel_consumer_context";

    public static Degrade CLIENT_DEFAULT_DEGRADE = new Degrade();

    public static Flow CLIENT_DEFAULT_FLOW = new Flow();


    public static class Degrade {
        /**
         * 是否默认启用
         */
        private boolean enabled = true;
        /**
         * 熔断策略(0: average RT, 1: exception ratio, 2: exception count). 由于
         */
        private int grade = 0;

        /**
         * 慢调用比例模式下为慢调用临界 RT（单位ms,超出该值计为慢调用）；异常比例/异常数模式下为对应的阈值
         */
        private double count = 2000;

        /**
         * 慢调用比例阈值，仅慢调用比例模式有效
         */
        private double slowRatioThreshold = 0.5;

        /**
         * 熔断触发的最小请求数
         */
        private int minRequestAmount = 5;

        /**
         * 熔断时长，单位为 s
         */
        private int timeWindow = 30;

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public double getCount() {
            return count;
        }

        public void setCount(double count) {
            this.count = count;
        }

        public double getSlowRatioThreshold() {
            return slowRatioThreshold;
        }

        public void setSlowRatioThreshold(double slowRatioThreshold) {
            this.slowRatioThreshold = slowRatioThreshold;
        }

        public int getMinRequestAmount() {
            return minRequestAmount;
        }

        public void setMinRequestAmount(int minRequestAmount) {
            this.minRequestAmount = minRequestAmount;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getTimeWindow() {
            return timeWindow;
        }

        public void setTimeWindow(int timeWindow) {
            this.timeWindow = timeWindow;
        }
    }

    public static class Flow {
        /**
         * 是否默认启用
         */
        private boolean enabled = true;
        /**
         * 限流阈值类型，线程数模式或QPS  (0: thread count, 1: QPS). 由于
         */
        private int grade = RuleConstant.FLOW_GRADE_THREAD;

        /**
         * Flow control threshold count.
         * 如果grade为qps，那么为resource能成熟的最大qps
         * 如果grade为thread，那么为resource能成熟的最大并发
         */
        private double count = 32;

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public double getCount() {
            return count;
        }

        public void setCount(double count) {
            this.count = count;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
