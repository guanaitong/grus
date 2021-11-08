/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.HttpCookie;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by August.Zhou on 2018-11-15 13:19.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SessionApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SessionTests {
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
    public void test() {
        String randomText = String.valueOf(Math.random());
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                this.base.toString() + "/write?r=" + randomText, String.class, "");
        HttpHeaders headers = response.getHeaders();
        Optional<String> any = headers.get("Set-Cookie").stream().findAny();
        Assertions.assertTrue(any.isPresent());
        HttpCookie gsessionId = HttpCookie.parse(any.get()).stream().filter(httpCookie -> httpCookie.getName().equals(SessionManager.SESSION_ID_COOKIE_NAME)).findAny().get();

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.put("Cookie", Arrays.asList(gsessionId.toString()));
        HttpEntity httpEntity = new HttpEntity(multiValueMap);
        ResponseEntity<String> response2 = this.restTemplate.exchange(this.base.toString() + "/read", HttpMethod.GET, httpEntity, String.class, "");
        Assertions.assertEquals(randomText, response2.getBody());
    }


}
