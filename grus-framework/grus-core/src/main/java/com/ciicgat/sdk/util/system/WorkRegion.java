/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by August.Zhou on 2017/12/14 9:49.
 */
public enum WorkRegion {
    DEV_OFC("dev-ofc", 3, WorkEnv.DEVELOP, WorkIdc.OFC, "dev"),
    TEST_ALI("test-ali", 18, WorkEnv.TEST, WorkIdc.ALI, "tech"),
    STAGE_SH("stage-sh", 11, WorkEnv.PREPARE, WorkIdc.SH, "com"),
    PROD_SH("prod-sh", 1, WorkEnv.PRODUCT, WorkIdc.SH, "com"),
    STAGE_LYRA("stage-lyra", 30, WorkEnv.PREPARE, WorkIdc.LYRA, "cn"),
    PROD_LYRA("prod-lyra", 31, WorkEnv.PRODUCT, WorkIdc.LYRA, "cn");


    private static final Logger LOGGER = LoggerFactory.getLogger(WorkRegion.class);
    private static final WorkRegion CURRENT_WORK_REGION;

    static {
        String work_region = System.getenv("WORK_REGION");
        LOGGER.info("work_region = {}", work_region);
        CURRENT_WORK_REGION = getWorkRegionByName(work_region);
    }

    private final WorkEnv workEnv;
    private final WorkIdc workIdc;
    private final String publicDomainSuffix;
    private final String serviceDomainSuffix;
    private final String serverDomainSuffix;

    private final String region;
    private final int regionId;

    WorkRegion(String region, int regionId, WorkEnv workEnv, WorkIdc workIdc, String publicDomainSuffix) {
        this.region = region;
        this.regionId = regionId;
        this.workEnv = workEnv;
        this.workIdc = workIdc;
        this.serverDomainSuffix = "servers." + workEnv.getInnerDomainSuffix() + "." + workIdc.getInnerDomainSuffix();
        this.serviceDomainSuffix = "services." + workEnv.getInnerDomainSuffix() + "." + workIdc.getInnerDomainSuffix();
        this.publicDomainSuffix = publicDomainSuffix;
    }

    public static WorkRegion getCurrentWorkRegion() {
        return CURRENT_WORK_REGION;
    }

    private static WorkRegion getWorkRegionByName(String region) {
        for (WorkRegion workRegion : WorkRegion.values()) {
            if (Objects.equals(workRegion.getRegion(), region)) {
                return workRegion;
            }
        }
        return WorkRegion.DEV_OFC;
    }

    public final String getRegion() {
        return region;
    }

    public final int getRegionId() {
        return regionId;
    }


    public final WorkEnv getWorkEnv() {
        return workEnv;
    }

    public final WorkIdc getWorkIdc() {
        return workIdc;
    }

    public final boolean isTest() {
        return workEnv == WorkEnv.TEST;
    }

    public final boolean isDevelop() {
        return workEnv == WorkEnv.DEVELOP;
    }

    public final boolean isDevelopOrTest() {
        return isDevelop() || isTest();
    }

    public final boolean isProduct() {
        return workEnv == WorkEnv.PRODUCT;
    }

    public final boolean isPrepare() {
        return workEnv == WorkEnv.PREPARE;
    }

    public final String getPublicDomainSuffix() {
        return publicDomainSuffix;
    }

    public final String getServiceDomainSuffix() {
        return serviceDomainSuffix;
    }

    public final String getServerDomainSuffix() {
        return serverDomainSuffix;
    }
}
