/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.feign;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by August.Zhou on 2019-04-02 9:45.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = FeignApplication.class,
        properties = {"spring.application.name=grus-demo", "grus.feign.log-req=true"})
public class FeignAutoConfigurationTests {

    @FeignService
    private PersonService personService;

    @FeignService(cacheBinding = @CacheBinding(method = "getPersonById", params = {0}), timeoutBinding = @TimeoutBinding(connectTimeoutMillis = 20 * 1000, readTimeoutMillis = 30 * 1000))
    private PersonService personService2;


    @Test
    public void test() {
        Assert.assertNotNull(personService);
        Assert.assertNotNull(personService2);
        Assert.assertSame(personService2.getPersonById(1), personService2.getPersonById(1));
    }

}
