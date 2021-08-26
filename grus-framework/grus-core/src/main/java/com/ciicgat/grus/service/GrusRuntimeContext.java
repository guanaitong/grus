/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.service;

import com.ciicgat.sdk.util.system.WorkEnv;
import com.ciicgat.sdk.util.system.WorkIdc;

/**
 * Created by August.Zhou on 2019-03-05 13:43.
 */
public interface GrusRuntimeContext {


    /**
     * 获取当前服务
     *
     * @return
     */
    GrusService selfService();

    /**
     * 获取当前应用名
     *
     * @return
     */
    String getAppName();

    /**
     * 获取当前应用在当前环境的secret
     *
     * @return
     */
    String getAppSecret();

    /**
     * 获取当前应用实例
     *
     * @return
     */
    String getAppInstance();

    /**
     * 获取当前的主机名
     *
     * @return
     */
    String getHostName();

    /**
     * 获取当前的hostIp
     *
     * @return
     */
    String getHostIp();

    /**
     * 获取当前的环境
     *
     * @return
     */
    WorkEnv getWorkEnv();

    /**
     * 获取当前的idc
     *
     * @return
     */
    WorkIdc getWorkIdc();


    /**
     * 获取应用启动时间
     *
     * @return
     */
    long getStartupDate();
}
