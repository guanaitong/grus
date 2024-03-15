/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.lock;

import com.ciicgat.grus.lock.DistLock;
import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.lang.tool.SessionIdGenerator;
import com.ciicgat.sdk.redis.RedisExecutor;
import com.ciicgat.sdk.util.system.Systems;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 使用redis实现的高可用分布式锁:
 * https://redis.io/topics/distlock
 * Created by August.Zhou on 2019/7/30 14:36.
 */
public class RedisLock implements DistLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLock.class);
    private static final String SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    private static final Long LOCK_RELEASE_OK = 1L;

    //这个时间，也是独占锁的最长时间。如果申请完锁后很快就释放了，实际的lock会小于你设置的maxLockTime。如果忘记释放或者程序崩溃，那么会等待maxLockTime的时间自动释放。
    private static final long REDIS_EXPIRE_TIME = TimeUnit.HOURS.toMillis(1);
    private static final String LOCK_SUCCESS = "OK";
    private final SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
    private final RedisExecutor redisExecutor;


    public RedisLock(RedisExecutor redisExecutor) {
        this.redisExecutor = redisExecutor;
    }

    /**
     * @param redisKey
     * @param timeout  获取锁的超时时间，为0的时候，表示获取锁失败就立即返回
     * @param timeUnit
     * @return 返回lockValue，用于释放锁。如果为null，说明获取锁失败。
     */
    private String tryAcquire(final String redisKey, final long timeout, final TimeUnit timeUnit) {
        //生成一个随机的值，用于释放key的时候比较
        String redisValue = sessionIdGenerator.generateSessionId();
        //类似于乐观锁的原理，此处优先获取一次锁试试。如果失败，再进入下面循环
        if (lock(redisKey, redisValue)) {
            return redisValue;
        }
        if (timeout == -1) {
            return null;
        } else if (timeout == 0) { //无超时
            while (true) {
                Threads.sleep(100);
                if (lock(redisKey, redisValue)) {
                    return redisValue;
                }
            }
        } else { //有超时
            long start = System.currentTimeMillis();
            long timeoutMs = timeUnit.toMillis(timeout);

            while (true) {
                Threads.sleep(100);
                if (System.currentTimeMillis() - start > timeoutMs) {
                    LOGGER.warn("tryAcquire lock failed for key {} after {} ms", redisKey, timeUnit.toMillis(timeout));
                    return null;
                }
                if (lock(redisKey, redisValue)) {
                    return redisValue;
                }
            }
        }
    }

    @Override
    public void execute(final String lockKey, Runnable runnable) {
        tryExecute(lockKey, 0, TimeUnit.MILLISECONDS, runnable);
    }

    @Override
    public boolean tryExecuteOnce(String lockKey, Runnable runnable) {
        return tryExecute(lockKey, -1, TimeUnit.MILLISECONDS, runnable);
    }

    @Override
    public boolean tryExecute(String lockKey, long time, TimeUnit unit, Runnable runnable) {
        Objects.requireNonNull(lockKey);
        String redisKey = "LOCK_" + Systems.APP_NAME + "__" + lockKey;
        String v = tryAcquire(redisKey, time, unit);
        if (StringUtils.isEmpty(v)) {
            return false;
        }
        try {
            runnable.run();
        } finally {
            unlock(redisKey, v);
        }
        return true;
    }

    private boolean lock(String redisKey, String redisValue) {
        String res = redisExecutor.execute(jedis -> jedis.set(redisKey, redisValue, SetParams.setParams().nx().px(REDIS_EXPIRE_TIME)));
        return LOCK_SUCCESS.equals(res);
    }

    private boolean unlock(String redisKey, String redisValue) {
        Object res = redisExecutor.execute(jedis -> jedis.eval(SCRIPT, Collections.singletonList(redisKey), Collections.singletonList(redisValue)));
        return LOCK_RELEASE_OK.equals(res);
    }
}
