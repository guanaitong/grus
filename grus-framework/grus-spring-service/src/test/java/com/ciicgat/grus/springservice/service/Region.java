/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.springservice.service;

import java.io.Serializable;
import java.util.Objects;

/**
 * 机房环境组合信息
 * Created by miracle on 2017/10/31
 */
public class Region implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 机房
     */
    private String idc;

    /**
     * 环境
     */
    private String env;

    /**
     * 排序id
     */
    private Integer sortId;

    public Region() {
    }

    public Region(Integer id, String idc, String env, Integer sortId) {
        this.id = id;
        this.idc = idc;
        this.env = env;
        this.sortId = sortId;
    }

    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdc() {
        return idc;
    }

    public void setIdc(String idc) {
        this.idc = idc;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Region region = (Region) o;

        if (!Objects.equals(id, region.id)) return false;
        if (!Objects.equals(idc, region.idc)) return false;
        if (!Objects.equals(env, region.env)) return false;
        return Objects.equals(sortId, region.sortId);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (idc != null ? idc.hashCode() : 0);
        result = 31 * result + (env != null ? env.hashCode() : 0);
        result = 31 * result + (sortId != null ? sortId.hashCode() : 0);
        return result;
    }
}
