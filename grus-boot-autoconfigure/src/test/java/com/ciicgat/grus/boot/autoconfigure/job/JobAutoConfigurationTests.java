/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.job;

import com.ciicgat.sdk.lang.threads.Threads;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by August.Zhou on 2019-04-08 13:44.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = JobApplication.class,
        properties = {"spring.application.name=grus-demo", "grus.gconf.appId=grus-demo",  "grus.zk.serverLists=app-zk.servers.dev.ofc:2181", "grus.job.namespace=grus-test-job"})
public class JobAutoConfigurationTests {


    @Autowired
    private GrusSimpleJob simpleJobTest;
    @Autowired
    private GrusSimpleJob2 simpleJobTest2;


    @Test
    public void test() {
        Threads.sleepSeconds(5);

        Assert.assertTrue(simpleJobTest.getValue() > 0);
        Assert.assertTrue(simpleJobTest2.getValue() > 0);

    }
}
