/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis.lock;

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
public class RedisLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLock.class);
    private static final String SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    private static final Long LOCK_RELEASE_OK = 1L;
    private static final String LOCK_SUCCESS = "OK";
    private final SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
    private final RedisExecutor redisExecutor;


    public RedisLock() {
        this(HARedis.getRedisExecutor());
    }

    public RedisLock(RedisExecutor redisExecutor) {
        this.redisExecutor = Objects.requireNonNull(redisExecutor);
    }

    public String tryAcquire(final String lockKey) {
        return tryAcquire(lockKey, 600, 0, TimeUnit.SECONDS);
    }

    public String tryAcquire(final String lockKey, final long timeout, final TimeUnit timeUnit) {
        return tryAcquire(lockKey, 600, timeout, timeUnit);
    }

    /**
     * @param lockKey
     * @param maxLockTime 独占锁的最长时间。如果申请完锁后很快就释放了，实际的lock会小于你设置的maxLockTime。如果忘记释放或者程序崩溃，那么会等待maxLockTime的时间自动释放。
     * @param timeout     获取锁的超时时间，为0的时候，表示获取锁失败就立即返回
     * @param timeUnit
     * @return 返回lockValue，用于释放锁。如果为null，说明获取锁失败。
     */
    public String tryAcquire(final String lockKey, final long maxLockTime, final long timeout, final TimeUnit timeUnit) {
        String redisKey = getRedisKey(lockKey);
        String redisValue = sessionIdGenerator.generateSessionId();
        long lockTimeMs = timeUnit.toMillis(maxLockTime);

        //类似于乐观锁的原理，此处优先获取一次锁试试。如果失败，再进入下面循环
        if (lock(redisKey, redisValue, lockTimeMs)) {
            return redisValue;
        }
        long timeoutMs = timeUnit.toMillis(timeout);

        while (true) {
            timeoutMs = timeoutMs - 500;
            if (timeoutMs <= 0) {
                LOGGER.warn("tryAcquire lock failed for key {} after {} ms", lockKey, timeUnit.toMillis(timeout));
                return null;
            }
            Threads.sleep(500);
            if (lock(redisKey, redisValue, lockTimeMs)) {
                return redisValue;
            }
        }
    }

    public boolean release(final String lockKey, final String lockValue) {
        Objects.requireNonNull(lockValue);
        String redisKey = getRedisKey(lockKey);
        return unlock(redisKey, lockValue);
    }

    /**
     * 推荐使用该方法，把锁的逻辑给封装掉了
     *
     * @param lockKey
     * @param runnable
     * @return 返回true表示获取了锁且runnable执行了
     */
    public boolean execute(final String lockKey, Runnable runnable) {
        String v = tryAcquire(lockKey);
        if (StringUtils.isEmpty(v)) {
            return false;
        }
        try {
            runnable.run();
        } finally {
            release(lockKey, v);
        }
        return true;
    }

    private String getRedisKey(final String lockKey) {
        Objects.requireNonNull(lockKey);
        return "LOCK_" + Systems.APP_NAME + "__" + lockKey;
    }

    private boolean lock(String redisKey, String redisValue, long redisExpireMs) {
        String res = redisExecutor.execute(jedis -> jedis.set(redisKey, redisValue, SetParams.setParams().nx().px(redisExpireMs)));
        return LOCK_SUCCESS.equals(res);
    }

    private boolean unlock(String redisKey, String redisValue) {
        Object res = redisExecutor.execute(jedis -> jedis.eval(SCRIPT, Collections.singletonList(redisKey), Collections.singletonList(redisValue)));
        return LOCK_RELEASE_OK.equals(res);
    }

}
