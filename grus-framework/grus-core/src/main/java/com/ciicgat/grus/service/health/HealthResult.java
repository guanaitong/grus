/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service.health;

/**
 * Created by August.Zhou on 2019-03-06 13:36.
 */
public class HealthResult {

    private String healthName;

    private boolean isHealthy;

    private String healthReport;


    public HealthResult() {

    }

    public HealthResult(String healthName) {
        this.healthName = healthName;
    }


    public String getHealthName() {
        return healthName;
    }

    public boolean isHealthy() {
        return isHealthy;
    }

    public void setHealthy(boolean isHealthy) {
        this.isHealthy = isHealthy;
    }

    public void setHealthName(String healthName) {
        this.healthName = healthName;
    }

    public String getHealthReport() {
        return healthReport;
    }

    public void setHealthReport(String healthReport) {
        this.healthReport = healthReport;
    }

}
