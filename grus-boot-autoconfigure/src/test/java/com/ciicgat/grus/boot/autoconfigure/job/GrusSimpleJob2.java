/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.job;

import io.elasticjob.lite.api.ShardingContext;
import io.elasticjob.lite.api.simple.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2019-04-08 13:45.
 */
@JobBean(jobName = "grusSimpleJob2", cron = "* * * ? * *")
public class GrusSimpleJob2 implements SimpleJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrusSimpleJob2.class);

    private AtomicInteger atomicInteger = new AtomicInteger();
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    public GrusSimpleJob2() {
        System.out.println("init----------------------------");
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        LOGGER.info(atomicInteger.incrementAndGet() + this.toString());
        countDownLatch.countDown();
    }

    public int getValue() throws InterruptedException {
        countDownLatch.await();
        return atomicInteger.get();
    }
}
