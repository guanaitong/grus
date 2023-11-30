/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.performance;

import com.ciicgat.grus.core.LatencyLevel;

import java.util.Map;

/**
 * Created by August.Zhou on 2023/11/30 12:44
 */
public class SlowLog {
    /**
     * 模块
     */
    private String module;

    /**
     * 级别
     */
    private LatencyLevel level;

    /**
     * 开始时间
     */
    private long ts;


    /**
     * 消耗时间
     */
    private long cost;

    private String detail;

    private int countLastMinute;

    private Map<String, Object> tags;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public LatencyLevel getLevel() {
        return level;
    }

    public void setLevel(LatencyLevel level) {
        this.level = level;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getCountLastMinute() {
        return countLastMinute;
    }

    public void setCountLastMinute(int countLastMinute) {
        this.countLastMinute = countLastMinute;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    public void setTags(Map<String, Object> tags) {
        this.tags = tags;
    }
}
