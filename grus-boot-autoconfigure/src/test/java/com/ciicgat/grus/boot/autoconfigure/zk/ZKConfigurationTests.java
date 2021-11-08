/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.zk;

import com.ciicgat.grus.idgen.IdGenerator;
import com.ciicgat.grus.lock.DistLock;
import com.ciicgat.grus.lock.DistLockFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

/**
 * Created by August.Zhou on 2019-04-08 13:44.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.application.name=grus-demo", "grus.idgen.dateFormat=yyMMdd", "grus.zk.serverLists=app-zk.servers.dev.ofc:2181", "grus.zk.namespace=grus-test-job"})
public class ZKConfigurationTests {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private DistLockFactory distLockFactory;

    @Test
    public void testIdGen() {
        String orderNo = idGenerator.makeNo();
        Assertions.assertNotNull(orderNo);

        long id = idGenerator.makeId();
        Assertions.assertTrue(id > 0);
    }

    @Test
    public void testLock() {
        DistLock distLock = distLockFactory.buildLock("unit-test");
        distLock.acquire();
        distLock.release();
        boolean result = distLock.tryAcquire(1, TimeUnit.SECONDS);
        Assertions.assertTrue(result);
        distLock.release();
    }
}
