/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.redis.lock;

import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.redis.RedisExecutor;
import com.ciicgat.sdk.util.system.Systems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.params.SetParams;

import java.util.Objects;

/**
 * 使用redis实现选举机制
 * Created by August.Zhou on 2018/7/5 14:36.
 */
public class RedisElect {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisElect.class);
    private static final String LOCK_SUCCESS = "OK";
    private final RedisExecutor redisExecutor;
    private volatile boolean isMaster = false;
    private volatile boolean isRunning = false;
    private String key;

    public RedisElect() {
        this(HARedis.getRedisExecutor(), Systems.APP_NAME);
    }

    public RedisElect(RedisExecutor redisExecutor, String key) {
        this.redisExecutor = Objects.requireNonNull(redisExecutor);
        this.key = "REDIS_ELECT_" + key;
    }

    public void startAcquire() {
        if (isRunning) {
            return;
        }
        synchronized (this) {
            if (isRunning) {
                return;
            }
            start0();
            isRunning = true;
        }
    }

    private void start0() {
        Threads.newDaemonThread(() -> {
            while (true) {
                String response = redisExecutor.execute(jedis -> jedis.set(key, "x", SetParams.setParams().nx().px(120_000)));
                if (LOCK_SUCCESS.equals(response)) {
                    isMaster = true;
                }
                if (isMaster) {
                    break;
                }
                Threads.sleepSeconds(60);
            }

            // 获取到master，一直更新key的过期时间，保证不被其他实例获取成功
            LOGGER.info("I'm master!!!");
            while (true) {
                try {
                    Threads.sleepSeconds(30);

                    redisExecutor.execute(jedis -> jedis.expire(key, 120));
                } catch (Exception e) {
                    LOGGER.error(key, e);
                }
            }
        }, "redis_refresh_master_key", Thread.MIN_PRIORITY).start();
    }

    public boolean isMaster() {
        return isMaster;
    }
}
