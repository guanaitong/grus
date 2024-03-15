/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.idgen;

import com.ciicgat.grus.idgen.WorkIdHolder;
import com.ciicgat.sdk.redis.RedisExecutor;
import com.ciicgat.sdk.util.system.Systems;

/**
 * @author August
 * @date 3/15/2024 2:25 PM
 */
public class RedisWorkIdHolder implements WorkIdHolder {

    private final RedisExecutor redisExecutor;

    public RedisWorkIdHolder(RedisExecutor redisExecutor) {
        this.redisExecutor = redisExecutor;
    }

    @Override
    public long getId() {
        String redisKey = "IDGEN_" + Systems.APP_NAME;
        return redisExecutor.execute(jedis -> jedis.incr(redisKey));
    }
}
