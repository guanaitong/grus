/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.integration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by August on 2021/8/31
 */
@SpringBootApplication
public class IntegrationApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(IntegrationApplication.class)
                .run(args);
    }
}
