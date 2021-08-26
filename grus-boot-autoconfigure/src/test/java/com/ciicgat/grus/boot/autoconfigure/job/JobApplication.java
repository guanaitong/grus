/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.job;

import com.ciicgat.grus.boot.autoconfigure.data.GrusDataAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.feign.FeignAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.opentracing.OpenTracingAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.redis.GrusRedisAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.swagger.SwaggerAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.validation.GrusValidationAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.validation.ValidationAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.web.GrusWebAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.zk.ZKAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by August.Zhou on 2019-04-08 13:43.
 */
@SpringBootApplication(exclude = {
        FeignAutoConfiguration.class,
        SwaggerAutoConfiguration.class,
        GrusDataAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        GrusWebAutoConfiguration.class,
        OpenTracingAutoConfiguration.class,
        ValidationAutoConfiguration.class,
        GrusValidationAutoConfiguration.class,
        GrusRedisAutoConfiguration.class,
        ZKAutoConfiguration.class})
@EnableElasticJob
public class JobApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(JobApplication.class)
                .run(args);

    }
}
