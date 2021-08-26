/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.api.core.model;

import java.util.List;

/**
 * Created by August.Zhou on 2017/8/1 13:46.
 */
public class PhpRespBean {
    private Integer id;
    private Integer subscriptionId;
    private Integer zoneId;
    private Integer appId;
    private String name;

    private List<Integer> allowMemberTypes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getAllowMemberTypes() {
        return allowMemberTypes;
    }

    public void setAllowMemberTypes(List<Integer> allowMemberTypes) {
        this.allowMemberTypes = allowMemberTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhpRespBean)) return false;

        PhpRespBean bean1 = (PhpRespBean) o;

        if (id != null ? !id.equals(bean1.id) : bean1.id != null) return false;
        if (subscriptionId != null ? !subscriptionId.equals(bean1.subscriptionId) : bean1.subscriptionId != null)
            return false;
        if (zoneId != null ? !zoneId.equals(bean1.zoneId) : bean1.zoneId != null) return false;
        if (appId != null ? !appId.equals(bean1.appId) : bean1.appId != null) return false;
        if (name != null ? !name.equals(bean1.name) : bean1.name != null) return false;
        return allowMemberTypes != null ? allowMemberTypes.equals(bean1.allowMemberTypes) : bean1.allowMemberTypes == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (subscriptionId != null ? subscriptionId.hashCode() : 0);
        result = 31 * result + (zoneId != null ? zoneId.hashCode() : 0);
        result = 31 * result + (appId != null ? appId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (allowMemberTypes != null ? allowMemberTypes.hashCode() : 0);
        return result;
    }
}
