/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.job;

import com.ciicgat.sdk.util.system.Systems;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * Created by August.Zhou on 2019-04-04 10:19.
 */
@ConfigurationProperties(prefix = "grus.job")
public class JobProperties {

    /**
     * zk里的命名空间。一般不用填写，默认使用应用名
     */
    private String namespace;

    @PostConstruct
    private void init() {
        if (!StringUtils.hasLength(namespace)) {
            namespace = Systems.APP_NAME;
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
