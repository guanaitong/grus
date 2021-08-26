/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

import com.ciicgat.sdk.gconf.annotation.BeanFieldKey;

import java.util.Objects;

/**
 * Created by August.Zhou on 2017/7/7 14:07.
 */
public class SessionProp {

    //    @BeanFieldKey("default_session_alive_time")
    private int defaultSessionAliveTime;


    private int maxSessionCookieAliveTime;


    private long maxSessionAliveTime;


    private int ipMaxCountOneHour;

    private int ipUaMaxCountOneHour;

    private int maxCountOneMinutes;


    @BeanFieldKey("max_count_one_minutes1")
    int maxCountOneMinutes1;

    public int getDefaultSessionAliveTime() {
        return defaultSessionAliveTime;
    }

    public void setDefaultSessionAliveTime(int defaultSessionAliveTime) {
        this.defaultSessionAliveTime = defaultSessionAliveTime;
    }

    public int getMaxSessionCookieAliveTime() {
        return maxSessionCookieAliveTime;
    }

    public void setMaxSessionCookieAliveTime(int maxSessionCookieAliveTime) {
        this.maxSessionCookieAliveTime = maxSessionCookieAliveTime;
    }

    public long getMaxSessionAliveTime() {
        return maxSessionAliveTime;
    }

    public void setMaxSessionAliveTime(long maxSessionAliveTime) {
        this.maxSessionAliveTime = maxSessionAliveTime;
    }

    public int getIpMaxCountOneHour() {
        return ipMaxCountOneHour;
    }

    public void setIpMaxCountOneHour(int ipMaxCountOneHour) {
        this.ipMaxCountOneHour = ipMaxCountOneHour;
    }

    public int getIpUaMaxCountOneHour() {
        return ipUaMaxCountOneHour;
    }

    public void setIpUaMaxCountOneHour(int ipUaMaxCountOneHour) {
        this.ipUaMaxCountOneHour = ipUaMaxCountOneHour;
    }

    public int getMaxCountOneMinutes() {
        return maxCountOneMinutes;
    }

    public void setMaxCountOneMinutes(int maxCountOneMinutes) {
        this.maxCountOneMinutes = maxCountOneMinutes;
    }

    public int getMaxCountOneMinutes1() {
        return maxCountOneMinutes1;
    }

    public void setMaxCountOneMinutes1(int maxCountOneMinutes1) {
        this.maxCountOneMinutes1 = maxCountOneMinutes1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionProp)) return false;
        SessionProp that = (SessionProp) o;
        return defaultSessionAliveTime == that.defaultSessionAliveTime &&
                maxSessionCookieAliveTime == that.maxSessionCookieAliveTime &&
                maxSessionAliveTime == that.maxSessionAliveTime &&
                ipMaxCountOneHour == that.ipMaxCountOneHour &&
                ipUaMaxCountOneHour == that.ipUaMaxCountOneHour &&
                maxCountOneMinutes == that.maxCountOneMinutes &&
                maxCountOneMinutes1 == that.maxCountOneMinutes1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultSessionAliveTime, maxSessionCookieAliveTime, maxSessionAliveTime, ipMaxCountOneHour, ipUaMaxCountOneHour, maxCountOneMinutes, maxCountOneMinutes1);
    }
}
