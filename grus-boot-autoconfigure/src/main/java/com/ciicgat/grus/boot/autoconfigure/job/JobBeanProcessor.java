/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.job;

import com.ciicgat.grus.job.TraceableJob;
import com.ciicgat.sdk.lang.threads.Threads;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.infra.exception.JobConfigurationException;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.settings.JobConfigurationAPIImpl;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
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
            throw new RuntimeException("bean class " + bean.getClass().getName() + " should implements org.apache.shardingsphere.elasticjob.simple.job.SimpleJob");
        }

        String jobName = jobBeanAnnotation.jobName();
        SimpleJob simpleJob = (SimpleJob) bean;

        JobConfiguration jobCoreConfiguration = JobConfiguration
                .newBuilder(jobName, jobBeanAnnotation.shardingTotalCount())
                .cron(jobBeanAnnotation.cron())
                .overwrite(true)
                .description(jobBeanAnnotation.description())
                .shardingItemParameters(jobBeanAnnotation.shardingItemParameters()).build();
        LOGGER.info("start init job {}", jobName);
        TraceableJob traceableJob = new TraceableJob(simpleJob);
        ScheduleJobBootstrap scheduleJobBootstrap = null;
        try {
            scheduleJobBootstrap = new ScheduleJobBootstrap(zookeeperRegistryCenter, traceableJob, jobCoreConfiguration);
            scheduleJobBootstrap.schedule();
        } catch (JobConfigurationException e) {
            if (e.getMessage().contains("Job conflict with register center")) {
                LOGGER.warn(e.getMessage());
                LOGGER.warn("job {} exist and conflict,remove it", jobName);
                new JobConfigurationAPIImpl(zookeeperRegistryCenter).removeJobConfiguration(jobName);
                LOGGER.warn("job {} removed", jobName);
                //这边固定sleep 3秒，等待zk客户端缓存失效
                Threads.sleepSeconds(3);
                //re init
                scheduleJobBootstrap.schedule();
            } else {
                throw e;
            }
        }
        LOGGER.info("end init job {}", jobName);
        configurableBeanFactory.registerSingleton(JOBSCHEDULER_BEAN_NAME_PREFIX + jobBeanAnnotation.jobName(), scheduleJobBootstrap);
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
        }

    }

}
