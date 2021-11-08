/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.job;

import com.ciicgat.sdk.util.system.Systems;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.samplers.RateLimitingSampler;
import io.opentracing.util.GlobalTracer;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.settings.JobConfigurationAPIImpl;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by August.Zhou on 2019-04-08 13:45.
 */
public class TraceableJobTest implements SimpleJob {
    private static Logger logger = LoggerFactory.getLogger(TraceableJobTest.class);

    private static AtomicInteger atomicInteger = new AtomicInteger();
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    @BeforeAll
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
        countDownLatch.countDown();
        System.out.println(1);


    }

    public static int getValue() throws InterruptedException {
        countDownLatch.await();
        return atomicInteger.get();
    }


    @Test
    public void test() throws InterruptedException {
        logger.info("test");
        ZookeeperRegistryCenter zookeeperRegistryCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("app-zk.servers.dev.ofc", "test2"));
        zookeeperRegistryCenter.init();

        TraceableJobTest simpleJob = new TraceableJobTest();

        String jobName = "testxxx-17";
        JobConfiguration jobCoreConfiguration = JobConfiguration
                .newBuilder(jobName, 1)
                .cron("* * * ? * *")
                .overwrite(true)
                .shardingItemParameters("").build();

//        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration
//                .newBuilder(new SimpleJobConfiguration(jobCoreConfiguration, simpleJob.getClass().getCanonicalName()))
//                .overwrite(true)
//                .build();
        new JobConfigurationAPIImpl(zookeeperRegistryCenter).removeJobConfiguration(jobName);
        ScheduleJobBootstrap scheduleJobBootstrap = new ScheduleJobBootstrap(zookeeperRegistryCenter, simpleJob, jobCoreConfiguration);
        scheduleJobBootstrap.schedule();


        new JobConfigurationAPIImpl(zookeeperRegistryCenter).removeJobConfiguration(jobName);
        ScheduleJobBootstrap scheduleJobBootstrap1 = new ScheduleJobBootstrap(zookeeperRegistryCenter, new TraceableJob(simpleJob), jobCoreConfiguration);
        scheduleJobBootstrap1.schedule();



//        SpringJobScheduler springJobScheduler = new SpringJobScheduler(traceableJob, zookeeperRegistryCenter, liteJobConfiguration);
//        JobScheduler springJobScheduler = new JobScheduler(zookeeperRegistryCenter,liteJobConfiguration ,new ElasticJobListener[0]);

//        springJobScheduler.init();


        Assertions.assertTrue(TraceableJobTest.getValue() > 0);
    }
}
