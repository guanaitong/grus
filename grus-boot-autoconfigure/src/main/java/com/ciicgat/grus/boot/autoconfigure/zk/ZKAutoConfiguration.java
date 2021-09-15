/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.zk;

import com.ciicgat.grus.boot.autoconfigure.core.IdGenProperties;
import com.ciicgat.grus.boot.autoconfigure.gconf.GconfAutoConfiguration;
import com.ciicgat.grus.idgen.IdGenerator;
import com.ciicgat.grus.idgen.SnowflakeIdGenerator;
import com.ciicgat.grus.idgen.WorkIdHolder;
import com.ciicgat.grus.lock.DistLockFactory;
import com.ciicgat.grus.zk.ZKUtils;
import com.ciicgat.grus.zk.idgen.ZKWorkIdHolder;
import com.ciicgat.grus.zk.lock.ZKDistLockFactory;
import com.ciicgat.sdk.util.system.Systems;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/26 17:17
 * @Description:
 */
@EnableConfigurationProperties({ZKProperties.class, IdGenProperties.class})
@ConditionalOnClass({ZKUtils.class})
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "grus.zk", value = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter({GconfAutoConfiguration.class})
public class ZKAutoConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(name = "curatorFramework")
    public CuratorFramework curatorFramework(ZKProperties zkProperties) {
        Objects.requireNonNull(zkProperties.getServerLists());
        return ZKUtils.init(zkProperties.getServerLists());
    }

    @Bean
    @ConditionalOnMissingBean(name = "workIdHolder")
    public WorkIdHolder workIdHolder(CuratorFramework curatorFramework) {
        return new ZKWorkIdHolder(curatorFramework, Systems.APP_NAME);
    }

    @Bean
    @ConditionalOnMissingBean(name = "idGenerator")
    public IdGenerator idGenerator(WorkIdHolder workIdHolder, IdGenProperties idGenProperties) {
        return new SnowflakeIdGenerator(workIdHolder, idGenProperties.getDateFormat());
    }

    @Bean
    @ConditionalOnMissingBean(name = "distLockFactory")
    public DistLockFactory distLockFactory(CuratorFramework curatorFramework) {
        return new ZKDistLockFactory(curatorFramework, Systems.APP_NAME);
    }

}
