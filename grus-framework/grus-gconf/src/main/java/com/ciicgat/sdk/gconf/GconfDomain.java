/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.gconf;

import com.ciicgat.sdk.util.system.Systems;
import com.ciicgat.sdk.util.system.WorkRegion;

/**
 * Created by August.Zhou on 2018/8/23 10:21.
 */
public class GconfDomain {

    public static final String DOMAIN;

    static {
        String domain = null;
        if (Systems.IN_K8S) {
            domain = "gconf.kube-system";
        } else {
            domain = "gconf." + WorkRegion.getCurrentWorkRegion().getServiceDomainSuffix();
        }

        DOMAIN = domain;

    }
}
