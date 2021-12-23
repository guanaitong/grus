/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.zk;

import com.ciicgat.grus.boot.autoconfigure.data.GrusDataAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.feign.FeignAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.job.ElasticJobAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.opentelemetry.OpenTelemetryAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.swagger.SwaggerAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.validation.ValidationAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.web.GrusWebAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @Auther: Jiaju Wei
 * @Date: 2019/12/30 15:45
 * @Description:
 */
@SpringBootApplication(exclude = {
        FeignAutoConfiguration.class,
        SwaggerAutoConfiguration.class,
        GrusDataAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        OpenTelemetryAutoConfiguration.class,
        GrusWebAutoConfiguration.class,
        ValidationAutoConfiguration.class,
        ElasticJobAutoConfiguration.class})
public class ZKApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ZKApplication.class)
                .run(args);
    }
}
