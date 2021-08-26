/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.util.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by August.Zhou on 2017/12/14 9:49.
 */
public class WorkRegion {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkRegion.class);
    private static final WorkRegion CURRENT_WORK_REGION;

    static {
        String work_env = System.getenv("WORK_ENV");
        String work_idc = System.getenv("WORK_IDC");
        LOGGER.info("work_env = {} , work_idc = {}", work_env, work_idc);
        CURRENT_WORK_REGION = new WorkRegion(WorkEnv.getWorkEnv(work_env), WorkIdc.getWorkIdc(work_idc));
    }

    private final WorkEnv workEnv;
    private final WorkIdc workIdc;
    private final String publicDomainSuffix;
    private final String serviceDomainSuffix;
    private final String serverDomainSuffix;

    private WorkRegion(WorkEnv workEnv, WorkIdc workIdc) {
        this.workEnv = workEnv;
        this.workIdc = workIdc;
        this.serverDomainSuffix = "servers." + workEnv.getInnerDomainSuffix() + "." + workIdc.getInnerDomainSuffix();
        this.serviceDomainSuffix = "services." + workEnv.getInnerDomainSuffix() + "." + workIdc.getInnerDomainSuffix();
        String publicDomainSuffix = "";
        if (workEnv == WorkEnv.DEVELOP) {
            publicDomainSuffix = "dev";
        } else if (workEnv == WorkEnv.PRODUCT || workEnv == WorkEnv.PREPARE) {
            publicDomainSuffix = "com";
        } else if (workEnv == WorkEnv.TEST && workIdc == WorkIdc.JX) {
            publicDomainSuffix = "cc";
        } else if (workEnv == WorkEnv.TEST && workIdc == WorkIdc.ALI) {
            publicDomainSuffix = "tech";
        }
        this.publicDomainSuffix = publicDomainSuffix;
    }

    public static WorkRegion getCurrentWorkRegion() {
        return CURRENT_WORK_REGION;
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
