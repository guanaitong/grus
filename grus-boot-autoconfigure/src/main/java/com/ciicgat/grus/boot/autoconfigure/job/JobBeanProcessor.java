/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.job;

import com.ciicgat.grus.job.TraceableJob;
import com.ciicgat.sdk.lang.threads.Threads;
import com.google.common.base.Optional;
import io.elasticjob.lite.api.simple.SimpleJob;
import io.elasticjob.lite.config.JobCoreConfiguration;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.config.simple.SimpleJobConfiguration;
import io.elasticjob.lite.exception.JobConfigurationException;
import io.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl;
import io.elasticjob.lite.lifecycle.internal.settings.JobSettingsAPIImpl;
import io.elasticjob.lite.reg.zookeeper.ZookeeperRegistryCenter;
import io.elasticjob.lite.spring.api.SpringJobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import java.util.Objects;

/**
 * Created by August.Zhou on 2019-04-08 13:04.
 */
public class JobBeanProcessor implements BeanPostProcessor, BeanFactoryAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobBeanProcessor.class);

    private static final String JOBSCHEDULER_BEAN_NAME_PREFIX = "jobScheduler_";
    private final ZookeeperRegistryCenter zookeeperRegistryCenter;
    private ConfigurableBeanFactory configurableBeanFactory;


    public JobBeanProcessor(ZookeeperRegistryCenter zookeeperRegistryCenter) {
        this.zookeeperRegistryCenter = zookeeperRegistryCenter;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        JobBean jobBeanAnnotation = bean.getClass().getAnnotation(JobBean.class);
        if (jobBeanAnnotation == null) {
            return bean;
        }
        Objects.requireNonNull(jobBeanAnnotation.jobName());
        Objects.requireNonNull(jobBeanAnnotation.cron());
        if (!(bean instanceof SimpleJob)) {
            throw new RuntimeException("bean class " + bean.getClass().getName() + " should implements io.elasticjob.lite.api.simple.SimpleJob");
        }

        String jobName = jobBeanAnnotation.jobName();
        SimpleJob simpleJob = (SimpleJob) bean;

        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration
                .newBuilder(jobName, jobBeanAnnotation.cron(), jobBeanAnnotation.shardingTotalCount())
                .description(jobBeanAnnotation.description())
                .shardingItemParameters(jobBeanAnnotation.shardingItemParameters()).build();

        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration
                .newBuilder(new SimpleJobConfiguration(jobCoreConfiguration, bean.getClass().getCanonicalName()))
                .overwrite(true)
                .build();

        LOGGER.info("start init job {}", jobName);
        TraceableJob traceableJob = new TraceableJob(simpleJob);
        SpringJobScheduler springJobScheduler = new SpringJobScheduler(traceableJob, zookeeperRegistryCenter, liteJobConfiguration);
        try {
            springJobScheduler.init();
        } catch (JobConfigurationException e) {
            if (e.getMessage().contains("Job conflict with register center")) {
                LOGGER.warn(e.getMessage());
                LOGGER.warn("job {} exist and conflict,remove it", jobName);

                new JobOperateAPIImpl(zookeeperRegistryCenter).remove(Optional.of(jobName), Optional.absent());
                new JobSettingsAPIImpl(zookeeperRegistryCenter).removeJobSettings(jobName);
                LOGGER.warn("job {} removed", jobName);
                //这边固定sleep 3秒，等待zk客户端缓存失效
                Threads.sleepSeconds(3);
                //re init
                springJobScheduler.init();
            } else {
                throw e;
            }
        }
        LOGGER.info("end init job {}", jobName);
        configurableBeanFactory.registerSingleton(JOBSCHEDULER_BEAN_NAME_PREFIX + jobBeanAnnotation.jobName(), springJobScheduler);
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
        }

    }

}
