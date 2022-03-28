/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.endpoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.core.SpringVersion;

import java.util.LinkedHashMap;
import java.util.Map;

@Endpoint(id = "version")
public class GrusVersionEndpoint {

    @Value("${git.commit.message.short}")
    private String commitMessage;

    @Value("${git.branch}")
    private String branch;

    @Value("${git.commit.id}")
    private String commitId;

    @Value("${git.build.version}")
    private String version;

    //表示读操作 用GET请求可以获取到
    @ReadOperation
    public Map<String, Object> version() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("java.version", System.getProperty("java.version"));
        map.put("spring.version", SpringVersion.getVersion());
        map.put("springBoot.version", SpringBootVersion.getVersion());
        map.put("grus.version", version);
        map.put("git.commitId", commitId);
        map.put("git.branch", branch);
        map.put("git.commitMessage", commitMessage);
        return map;
    }
}
