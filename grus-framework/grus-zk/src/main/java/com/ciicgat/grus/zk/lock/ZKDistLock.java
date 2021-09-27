/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.zk.lock;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.lock.DistLock;
import com.ciicgat.sdk.lang.exception.ZKRuntimeException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/2 10:48
 * @Description: 使用Curator封装的ZK分布式锁
 * http://zookeeper.apache.org/doc/r3.4.9/recipes.html
 */
class ZKDistLock implements DistLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKDistLock.class);

    private CuratorFramework curator;
    private InterProcessMutex mutex;
    private String lockPath;

    ZKDistLock(CuratorFramework curator, String lockPath) {
        this.curator = curator;
        this.lockPath = lockPath;
        this.mutex = new InterProcessMutex(curator, lockPath);
    }

    @Override
    public void acquire() {
        try {
            mutex.acquire();
        } catch (Exception e) {
            LOGGER.error("ZK_LOCK_ERR", e);
            Alert.send("ZK锁连接异常", e);
            throw new ZKRuntimeException(e);
        }
    }

    @Override
    public boolean tryAcquire(long time, TimeUnit unit) {
        try {
            return mutex.acquire(time, unit);
        } catch (Exception e) {
            LOGGER.error("ZK_LOCK_ERR", e);
            Alert.send("ZK锁连接异常", e);
            throw new ZKRuntimeException(e);
        }
    }

    @Override
    public void release() {
        try {
            mutex.release();
            // ZK版本暂不支持Container节点的新特性，需要手动删除父节点，会有小概率的删除失败
            curator.delete().inBackground().forPath(lockPath);
        } catch (Exception e) {
            LOGGER.warn("ZK_UNLOCK_ERR", e);
        }
    }
}
