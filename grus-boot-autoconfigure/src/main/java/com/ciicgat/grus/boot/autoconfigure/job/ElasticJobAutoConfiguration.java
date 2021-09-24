/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.job;

import com.ciicgat.grus.boot.autoconfigure.condition.ConditionalOnWorkEnv;
import com.ciicgat.grus.boot.autoconfigure.gconf.GconfAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.zk.ZKProperties;
import com.ciicgat.grus.job.TraceableJob;
import com.ciicgat.sdk.util.system.WorkEnv;
import org.apache.shardingsphere.elasticjob.lite.internal.schedule.JobScheduler;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * Created by August.Zhou on 2019-01-28 17:55.
 */
@EnableConfigurationProperties({JobProperties.class, ZKProperties.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({TraceableJob.class, JobScheduler.class})
@ConditionalOnProperty(prefix = "grus.job", value = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter({GconfAutoConfiguration.class})
@ConditionalOnWorkEnv({WorkEnv.DEVELOP, WorkEnv.TEST, WorkEnv.PRODUCT})
public class ElasticJobAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticJobAutoConfiguration.class);


    @Bean(value = {"zookeeperRegistryCenter", "coordinatorRegistryCenter"}, initMethod = "init", destroyMethod = "close")
    @ConditionalOnMissingBean
    public ZookeeperRegistryCenter zookeeperRegistryCenter(JobProperties jobProperties, ZKProperties zkProperties) {
        var serverLists = zkProperties.getServerLists();
        String namespace = jobProperties.getNamespace();
        LOGGER.info("use serverLists {},namespace {}", serverLists, namespace);
        Objects.requireNonNull(serverLists);
        Objects.requireNonNull(namespace);
        var zookeeperRegistryCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration(serverLists, namespace));
        return zookeeperRegistryCenter;
    }

    @Bean
    public JobBeanProcessor jobBeanProcessor(ZookeeperRegistryCenter zookeeperRegistryCenter) {
        return new JobBeanProcessor(zookeeperRegistryCenter);
    }


}
