/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.gconf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by August.Zhou on 2019-02-21 14:47.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = GconfApplication.class,
        properties = {"spring.application.name=grus-demo", "grus.gconf.appId=grus-demo"})
public class GconfAutoConfigurationTests {

//    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
//            .withInitializer(new GconfContextInitializer());
//
//
//    @Test
//    public void testCustomConfig() {
//        this.contextRunner
//                .withParent(new AnnotationConfigApplicationContext(RedisPropClazz.class))
//                .withPropertyValues("grus.gconf.appId:userdoor")
//                .run(context -> {
////                        RedisPropClazz redisPropClazz = context.getBean(RedisPropClazz.class);
////                        assertThat(redisPropClazz.getMyHost() != null).isTrue();
//                });
//    }


    @Autowired
    private RedisPropClazz redisPropClazz;

    @Autowired
    private RedisPropWithValue redisPropWithValue;

    @Autowired
    private Environment environment;


    @Test
    public void test() {
        Assert.assertNotNull(redisPropClazz.getMyHost());

        Assert.assertEquals(redisPropClazz.getMyHost(), redisPropWithValue.getMyHost());
    }

    @Test
    public void testLocalConfig() {
        Assert.assertNotNull(environment.getProperty("local.name"));

        Assert.assertEquals("test", environment.getRequiredProperty("local.name"));
    }

}
