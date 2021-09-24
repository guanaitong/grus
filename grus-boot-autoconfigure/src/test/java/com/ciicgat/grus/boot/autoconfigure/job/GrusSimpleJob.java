/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2019-04-08 13:45.
 */
@JobBean(jobName = "grusSimpleJob", cron = "* * * ? * *")
public class GrusSimpleJob implements SimpleJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrusSimpleJob.class);

    private AtomicInteger atomicInteger = new AtomicInteger();
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public GrusSimpleJob() {
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
