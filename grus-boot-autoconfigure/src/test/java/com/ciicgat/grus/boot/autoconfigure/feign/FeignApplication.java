/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.feign;

import com.ciicgat.grus.boot.autoconfigure.data.GrusDataAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.gconf.GconfAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.job.ElasticJobAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.opentelemetry.OpenTelemetryAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.swagger.SwaggerAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.validation.ValidationAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.web.GrusWebAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.zk.ZKAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by August.Zhou on 2019-04-02 9:43.
 */
@SpringBootApplication(exclude = {
        GconfAutoConfiguration.class,
        SwaggerAutoConfiguration.class,
        GrusDataAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        OpenTelemetryAutoConfiguration.class,
        GrusWebAutoConfiguration.class,
        ValidationAutoConfiguration.class,
        ElasticJobAutoConfiguration.class,
        ZKAutoConfiguration.class})
@EnableFeign
public class FeignApplication {


    public static void main(String[] args) {
        new SpringApplicationBuilder(FeignApplication.class)
                .run(args);

    }


}
