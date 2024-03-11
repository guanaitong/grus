/*
 * Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.rabbitmq;

import com.ciicgat.grus.boot.autoconfigure.data.GrusDataAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.data.GrusPageHelperAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.elasticsearch.ElasticsearchAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.feign.FeignAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.job.ElasticJobAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.opentelemetry.OpenTelemetryAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.swagger.SwaggerAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.validation.ValidationAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.web.GrusWebAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.zk.ZKAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by August.Zhou on 2022/7/8 14:14
 */
@SpringBootApplication(exclude = {
        FeignAutoConfiguration.class,
        SwaggerAutoConfiguration.class,
        GrusDataAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        OpenTelemetryAutoConfiguration.class,
        GrusWebAutoConfiguration.class,
        ValidationAutoConfiguration.class,
        GrusPageHelperAutoConfiguration.class,
        ZKAutoConfiguration.class,
        ElasticsearchAutoConfiguration.class,
        QuartzAutoConfiguration.class,
        ElasticJobAutoConfiguration.class})
public class RabbitMqApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RabbitMqApplication.class)
                .run(args);
    }

}
