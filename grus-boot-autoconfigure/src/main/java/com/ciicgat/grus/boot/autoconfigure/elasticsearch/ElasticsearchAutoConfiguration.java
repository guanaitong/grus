/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.elasticsearch;

import com.ciicgat.grus.elasticsearch.core.ElasticsearchTemplate;
import com.ciicgat.grus.elasticsearch.core.RestClients;
import com.google.common.base.Preconditions;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by August.Zhou on 2019-09-09 17:59.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RestHighLevelClient.class, ElasticsearchTemplate.class})
@EnableConfigurationProperties({ElasticsearchProperties.class})
public class ElasticsearchAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchAutoConfiguration.class);

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public RestHighLevelClient restHighLevelClient(ElasticsearchProperties elasticsearchProperties) {
        LOGGER.info("开始建立es连接............");
        List<HttpHost> httpHosts = new ArrayList<>(elasticsearchProperties.getNodes().size());
        for (String node : elasticsearchProperties.getNodes()) {
            String[] s = node.split(":");
            Assert.state(s.length == 2, "Must be defined as 'host:port'");
            httpHosts.add(new HttpHost(s[0], Integer.parseInt(s[1])));
        }
        RestHighLevelClient client = new RestHighLevelClient(RestClients.builder(httpHosts.toArray(new HttpHost[0])));

        int nodeSize = client.getLowLevelClient().getNodes().size();
        LOGGER.info("nodeSize {}", nodeSize);
        Preconditions.checkArgument(nodeSize >= 1, "this is no available node");
        LOGGER.info("连接es已经成功");
        return client;
    }
}
