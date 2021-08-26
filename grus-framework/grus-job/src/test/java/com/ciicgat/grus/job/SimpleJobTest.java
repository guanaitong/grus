/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.job;

import com.ciicgat.sdk.lang.threads.Threads;
import com.ciicgat.sdk.util.system.Systems;
import io.elasticjob.lite.api.ShardingContext;
import io.elasticjob.lite.api.simple.SimpleJob;
import io.elasticjob.lite.config.JobCoreConfiguration;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.config.simple.SimpleJobConfiguration;
import io.elasticjob.lite.reg.zookeeper.ZookeeperConfiguration;
import io.elasticjob.lite.reg.zookeeper.ZookeeperRegistryCenter;
import io.elasticjob.lite.spring.api.SpringJobScheduler;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.samplers.RateLimitingSampler;
import io.opentracing.util.GlobalTracer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2019-04-08 13:45.
 */
public class SimpleJobTest implements SimpleJob {
    private static Logger logger = LoggerFactory.getLogger(SimpleJobTest.class);

    private static AtomicInteger atomicInteger = new AtomicInteger();

    @BeforeClass
    public static void registerJaegerTracer() {
        Configuration.SenderConfiguration senderConfiguration =
                new Configuration
                        .SenderConfiguration()
                        .withAgentHost("127.0.0.1")
                        .withAgentPort(6831);

        Configuration.ReporterConfiguration reporterConfig =
                new Configuration
                        .ReporterConfiguration()
                        .withSender(senderConfiguration)
                        .withLogSpans(false);

        Float parm = "unknown".equals(Systems.APP_NAME) ? 0f : 50f;
        //采样配置
        Configuration.SamplerConfiguration samplerConfig =
                new Configuration
                        .SamplerConfiguration()
                        .withType(RateLimitingSampler.TYPE)
                        .withParam(parm);

        JaegerTracer tracer = new Configuration(Systems.APP_NAME).withSampler(samplerConfig).withReporter(reporterConfig).getTracer();
        GlobalTracer.register(tracer);
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        atomicInteger.incrementAndGet();
        System.out.println(1);


    }

    public static int getValue() {
        return atomicInteger.get();
    }


    @org.junit.Test
    public void test() {
        logger.info("test");
        ZookeeperRegistryCenter zookeeperRegistryCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("app-zk.servers.dev.ofc", "test2"));
        zookeeperRegistryCenter.init();

        SimpleJobTest simpleJob = new SimpleJobTest();

        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration
                .newBuilder("name", "* * * ? * *", 1)
                .shardingItemParameters("").build();

        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration
                .newBuilder(new SimpleJobConfiguration(jobCoreConfiguration, simpleJob.getClass().getCanonicalName()))
                .overwrite(true)
                .build();

        TraceableJob traceableJob = new TraceableJob(simpleJob);

        SpringJobScheduler springJobScheduler = new SpringJobScheduler(traceableJob, zookeeperRegistryCenter, liteJobConfiguration);
//        JobScheduler springJobScheduler = new JobScheduler(zookeeperRegistryCenter,liteJobConfiguration ,new ElasticJobListener[0]);

        springJobScheduler.init();

        Threads.sleepSeconds(10);

        Assert.assertTrue(SimpleJobTest.getValue() > 0);
    }
}
