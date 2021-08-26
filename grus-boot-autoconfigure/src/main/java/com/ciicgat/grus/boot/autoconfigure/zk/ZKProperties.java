/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.zk;

import com.ciicgat.sdk.gconf.GlobalGconfConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/26 17:14
 * @Description:
 */
@ConfigurationProperties(prefix = "grus.zk")
public class ZKProperties {
    /**
     * 连接Zookeeper服务器的列表.
     * 一般不用填写。默认去gconf统一配置的地方加载
     * 包括IP地址和端口号.
     * 多个地址用逗号分隔.
     * 如: host1:2181,host2:2181
     */
    private String serverLists;


    @PostConstruct
    private void init() {
        if (!StringUtils.hasLength(serverLists)) {
            serverLists = GlobalGconfConfig.getConfig().getProperties("address.properties").getProperty("zkServerLists");
        }
    }


    public String getServerLists() {
        return serverLists;
    }

    public void setServerLists(String serverLists) {
        this.serverLists = serverLists;
    }
}
