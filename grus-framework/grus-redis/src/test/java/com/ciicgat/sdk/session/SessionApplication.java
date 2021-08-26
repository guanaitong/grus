/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.sdk.session;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by August.Zhou on 2018-11-15 13:20.
 */
@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
@ComponentScan("com.ciicgat.sdk.session")
public class SessionApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SessionApplication.class)
                .run(args);

    }
}
