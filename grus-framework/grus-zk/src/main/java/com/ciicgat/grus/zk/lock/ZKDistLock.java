/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.zk.lock;

import com.ciicgat.grus.alert.Alert;
import com.ciicgat.grus.lock.DistLock;
import com.ciicgat.grus.zk.ZKConstants;
import com.ciicgat.sdk.lang.exception.ZKRuntimeException;
import com.ciicgat.sdk.util.system.Systems;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: Jiaju Wei
 * @Date: 2020/1/2 10:48
 * @Description: 使用Curator封装的ZK分布式锁
 * http://zookeeper.apache.org/doc/r3.4.9/recipes.html
 */
public class ZKDistLock implements DistLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKDistLock.class);

    private final CuratorFramework curator;

    public ZKDistLock(CuratorFramework curator) {
        this.curator = curator;
    }

    @Override
    public void execute(String lockKey, Runnable runnable) {
        Objects.requireNonNull(lockKey);
        String lockPath = ZKPaths.makePath(ZKConstants.LOCK_PATH, Systems.APP_NAME, lockKey);
        InterProcessMutex mutex = new InterProcessMutex(curator, lockPath);
        try {
            mutex.acquire();
        } catch (Exception e) {
            LOGGER.error("ZK_LOCK_ERR", e);
            Alert.send("ZK锁连接异常", e);
            throw new ZKRuntimeException(e);
        }
        try {
            runnable.run();
        } finally {
            release(mutex, lockPath);
        }
    }

    // 基于zk的实现，无法精准实现once语意
    // 默认采用1s钟较低的延迟，实现类似的效果
    @Override
    public boolean tryExecuteOnce(String lockKey, Runnable runnable) {
        return tryExecute(lockKey, 3, TimeUnit.SECONDS, runnable);
    }

    @Override
    public boolean tryExecute(String lockKey, long time, TimeUnit unit, Runnable runnable) {
        Objects.requireNonNull(lockKey);
        String lockPath = ZKPaths.makePath(ZKConstants.LOCK_PATH, Systems.APP_NAME, lockKey);
        InterProcessMutex mutex = new InterProcessMutex(curator, lockPath);
        boolean acquireResult;
        try {
            acquireResult = mutex.acquire(time, unit);
        } catch (Exception e) {
            LOGGER.error("ZK_LOCK_ERR", e);
            Alert.send("ZK锁连接异常", e);
            throw new ZKRuntimeException(e);
        }
        if (!acquireResult) {
            LOGGER.warn("tryAcquire lock failed for key {} after {} ms", lockKey, unit.toMillis(time));
            return false;
        }
        try {
            runnable.run();
            return true;
        } finally {
            release(mutex, lockPath);
        }
    }

    private void release(InterProcessMutex mutex, String lockPath) {
        try {
            mutex.release();
            // 原来ZK版本暂不支持Container节点的新特性，需要手动删除父节点，会有小概率的删除失败
//            curator.delete().inBackground().forPath(lockPath);
        } catch (Exception e) {
            LOGGER.warn("ZK_UNLOCK_ERR", e);
        }
    }
}
