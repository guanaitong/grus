/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.gconf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Created by August.Zhou on 2019-02-21 14:47.
 */
@ExtendWith(SpringExtension.class)
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
        Assertions.assertNotNull(redisPropClazz.getMyHost());

        Assertions.assertEquals(redisPropClazz.getMyHost(), redisPropWithValue.getMyHost());
    }

    @Test
    public void testLocalConfig() {
        Assertions.assertNotNull(environment.getProperty("local.name"));

        Assertions.assertEquals("test", environment.getRequiredProperty("local.name"));
    }

}
