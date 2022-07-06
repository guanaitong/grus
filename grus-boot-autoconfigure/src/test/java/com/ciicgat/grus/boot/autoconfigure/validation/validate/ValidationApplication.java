/*
 * Copyright 2007-2022, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.validate;

import com.ciicgat.grus.boot.autoconfigure.data.GrusDataAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.elasticsearch.ElasticsearchAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.feign.FeignAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.gconf.GconfAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.job.ElasticJobAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.opentelemetry.OpenTelemetryAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.rabbitmq.RabbitMQAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.swagger.SwaggerAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.web.GrusWebAutoConfiguration;
import com.ciicgat.grus.boot.autoconfigure.zk.ZKAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by August.Zhou on 2019-04-10 17:45.
 */
@SpringBootApplication(exclude = {
        GconfAutoConfiguration.class,
        FeignAutoConfiguration.class,
        SwaggerAutoConfiguration.class,
        GrusDataAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        GrusWebAutoConfiguration.class,
        OpenTelemetryAutoConfiguration.class,
        RabbitMQAutoConfiguration.class,
        ElasticJobAutoConfiguration.class,
        ElasticsearchAutoConfiguration.class,
        QuartzAutoConfiguration.class,
        ZKAutoConfiguration.class})
@EnableAsync(proxyTargetClass = true)
public class ValidationApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ValidationApplication.class)
                .run(args);
    }

}
