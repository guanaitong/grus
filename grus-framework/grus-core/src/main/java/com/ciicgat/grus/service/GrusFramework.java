/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service;

import com.ciicgat.grus.service.impl.StandardGrusRuntimeContext;
import com.ciicgat.grus.service.impl.StandardGrusRuntimeManager;

/**
 * Created by August.Zhou on 2019-03-06 10:11.
 */
public class GrusFramework {


    private static volatile GrusRuntimeManager grusRuntimeManager = null;


    public static GrusRuntimeManager getGrusRuntimeManager() {
        if (grusRuntimeManager == null) {
            synchronized (GrusFramework.class) {
                if (grusRuntimeManager == null) {
                    grusRuntimeManager = new StandardGrusRuntimeManager(new StandardGrusRuntimeContext());
                }
            }
        }
        return grusRuntimeManager;
    }

    public static void setGrusRuntimeManager(GrusRuntimeManager grusRuntimeManager) {
        GrusFramework.grusRuntimeManager = grusRuntimeManager;
    }
}
