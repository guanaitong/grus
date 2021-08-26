/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.elasticsearch;

import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by August.Zhou on 2019-09-09 18:01.
 */
@ConfigurationProperties(prefix = "grus.elasticsearch")
public class ElasticsearchProperties {

    /**
     * 如果不填，采用框架默认配置。
     * Comma-separated list of "host:port" pairs to bootstrap from. This represents an
     * "initial" list of cluster nodes and is required to have at least one entry.
     */
    private List<String> nodes;

    @PostConstruct
    public void init() {
        if (nodes == null || nodes.size() == 0) {
            String esServerLists = GlobalGconfConfig.getConfig().getProperties("address.properties").getProperty("esServerLists");
            this.setNodes(List.of(esServerLists.split(",")));
        }
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }
}
