/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.zk.lock;

import com.ciicgat.grus.lock.DistLock;
import com.ciicgat.grus.lock.DistLockFactory;
import com.ciicgat.grus.zk.ZKConstants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/2 10:34
 * @Description: ZK锁生成工厂
 */
public class ZKDistLockFactory implements DistLockFactory {

    private CuratorFramework curator;

    private String appName;

    public ZKDistLockFactory(CuratorFramework curator, String appName) {
        this.curator = curator;
        this.appName = appName;
    }

    @Override
    public DistLock buildLock(String keyName) {
        String lockPath = ZKPaths.makePath(appName, ZKConstants.LOCK_PATH, keyName);
        return new ZKDistLock(curator, lockPath);
    }
}
