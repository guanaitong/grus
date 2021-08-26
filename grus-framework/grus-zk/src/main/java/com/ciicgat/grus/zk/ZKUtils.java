/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;

import static com.ciicgat.grus.zk.ZKConstants.CONNECT_TIMEOUT_MS;
import static com.ciicgat.grus.zk.ZKConstants.RETRY_INTERVAL_MS;
import static com.ciicgat.grus.zk.ZKConstants.RETRY_MAX_MS;
import static com.ciicgat.grus.zk.ZKConstants.SESSION_TIMEOUT_MS;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/18 15:56
 * @Description:
 */
public class ZKUtils {

    public static CuratorFramework init(String connectionString) {
        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString(connectionString)
                .retryPolicy(new RetryUntilElapsed(RETRY_MAX_MS, RETRY_INTERVAL_MS))
                .connectionTimeoutMs(CONNECT_TIMEOUT_MS)
                .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                .build();
        curator.start();

        return curator;
    }

}
