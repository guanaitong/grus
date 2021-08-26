/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.job;

import io.elasticjob.lite.api.ShardingContext;
import io.elasticjob.lite.api.simple.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2019-04-08 13:45.
 */
@JobBean(jobName = "grusSimpleJob", cron = "* * * ? * *")
public class GrusSimpleJob implements SimpleJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrusSimpleJob.class);

    private AtomicInteger atomicInteger = new AtomicInteger();

    public GrusSimpleJob() {
        System.out.println("init----------------------------");
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        LOGGER.info(atomicInteger.incrementAndGet() + this.toString());
    }

    public int getValue() {
        return atomicInteger.get();
    }
}
