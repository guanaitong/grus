/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.validate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URL;

/**
 * Created by Josh on 17-11-9.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = ValidationApplication.class,
        properties = {"spring.application.name=grus-demo", "grus.validation.errorCode=88888"})
public class ValidateTest {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() throws Exception {
        String url = String.format("http://127.0.0.1:%d/", port);
        System.out.println(String.format("port is : [%d]", port));
        this.base = new URL(url);
    }

    @Test
    public void testPrimitiveNotNullSuccess() {

        ResponseEntity<String> response = restTemplate.getForEntity(this.base.toString() + "/testPrimitive?testString=12&testLong=2", String.class, "");

        Assertions.assertTrue(response.getStatusCodeValue() == 200);
        Assertions.assertTrue(response.getBody().contains("success"));
    }

    @Test
    public void testPojoNotNullSuccess() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(this.base.toString() + "/testPojo?testString=12&testLong=1", String.class, "");

        Assertions.assertTrue(response.getStatusCodeValue() == 200);
        Assertions.assertTrue(response.getBody().contains("success"));

    }

    @Test
    public void testPojoNotMinFailed() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(this.base.toString() + "/testPojo?testString=12&testLong=0", String.class, "");

        Assertions.assertTrue(response.getStatusCodeValue() == 200);
        Assertions.assertTrue(response.getBody().contains("参数验证不通过"));

    }

}
