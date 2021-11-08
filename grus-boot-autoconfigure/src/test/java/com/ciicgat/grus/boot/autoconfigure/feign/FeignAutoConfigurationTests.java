/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.feign;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Created by August.Zhou on 2019-04-02 9:45.
 */
@ExtendWith(SpringExtension.class)
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
        Assertions.assertNotNull(personService);
        Assertions.assertNotNull(personService2);
        Assertions.assertSame(personService2.getPersonById(1), personService2.getPersonById(1));
    }

}
