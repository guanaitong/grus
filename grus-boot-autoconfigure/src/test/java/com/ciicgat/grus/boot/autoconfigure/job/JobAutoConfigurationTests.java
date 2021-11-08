/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.job;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Created by August.Zhou on 2019-04-08 13:44.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = JobApplication.class,
        properties = {"spring.application.name=grus-demo", "grus.gconf.appId=grus-demo",  "grus.zk.serverLists=app-zk.servers.dev.ofc:2181", "grus.job.namespace=grus-test-job"})
public class JobAutoConfigurationTests {


    @Autowired
    private GrusSimpleJob simpleJobTest;
    @Autowired
    private GrusSimpleJob2 simpleJobTest2;


    @Test
    public void test() throws InterruptedException {
        Assertions.assertTrue(simpleJobTest.getValue() > 0);
        Assertions.assertTrue(simpleJobTest2.getValue() > 0);
    }
}
