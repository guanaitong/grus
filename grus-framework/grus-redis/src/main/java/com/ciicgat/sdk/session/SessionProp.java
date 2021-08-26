/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.session;


import com.ciicgat.sdk.gconf.annotation.BeanFieldKey;

import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2017/7/7 14:07.
 */
public class SessionProp {

    @BeanFieldKey("default_session_alive_time")
    private int defaultSessionAliveTime = (int) TimeUnit.HOURS.toSeconds(1);
    @BeanFieldKey("max_session_cookie_alive_time")
    private int maxSessionCookieAliveTime = (int) TimeUnit.HOURS.toSeconds(6);

    @BeanFieldKey("max_session_alive_time")
    private long maxSessionAliveTime = TimeUnit.HOURS.toMillis(6);


    @BeanFieldKey("ip_max_count_one_hour")
    private int ipMaxCountOneHour = 20000;
    @BeanFieldKey("ipua_max_count_one_hour")
    private int ipUaMaxCountOneHour = 10000;
    @BeanFieldKey("max_count_one_minutes")
    private int maxCountOneMinutes = 10000;
    @BeanFieldKey("max_count_one_hour")
    private int maxCountOneHour = 100000;


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

    public int getMaxCountOneHour() {
        return maxCountOneHour;
    }

    public void setMaxCountOneHour(int maxCountOneHour) {
        this.maxCountOneHour = maxCountOneHour;
    }
}
