/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import com.ciicgat.grus.boot.autoconfigure.core.GrusCoreContextInitializer;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August.Zhou on 2019-04-16 13:25.
 */
public class GrusDataAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new GrusCoreContextInitializer())
            .withUserConfiguration(GrusDataAutoConfiguration.class, GrusMybatisAutoConfiguration.class);

    @Test
    public void test1() {
        this.contextRunner
                .withPropertyValues("spring.application.name=grus-demo")
                .run(context -> {
                    Object masterDataSource = context.getBean("masterDataSource");
                    assertThat(masterDataSource).isInstanceOf(HikariDataSource.class);

                    Map<String, HikariDataSource> beansOfType = context.getBeansOfType(HikariDataSource.class);

                    assertThat(beansOfType.size()).isEqualTo(1);

                    Object writeSqlSessionTemplate = context.getBean("writeSqlSessionTemplate");
                    assertThat(writeSqlSessionTemplate).isInstanceOf(SqlSessionTemplate.class);
                    Map<String, SqlSessionTemplate> beansOfType2 = context.getBeansOfType(SqlSessionTemplate.class);

                    assertThat(beansOfType2.size()).isEqualTo(1);
                });
    }


    @Test
    public void test2() {
        this.contextRunner
                .withPropertyValues("spring.application.name=grus-demo", "grus.db.readWriteSeparation=true")
                .run(context -> {
                    Object masterDataSource = context.getBean("masterDataSource");
                    assertThat(masterDataSource).isInstanceOf(HikariDataSource.class);

                    Object slaveDataSource = context.getBean("slaveDataSource");
                    assertThat(slaveDataSource).isInstanceOf(HikariDataSource.class);

                    Map<String, HikariDataSource> beansOfType = context.getBeansOfType(HikariDataSource.class);

                    assertThat(beansOfType.size()).isEqualTo(2);

                    Object writeSqlSessionTemplate = context.getBean("writeSqlSessionTemplate");
                    assertThat(writeSqlSessionTemplate).isInstanceOf(SqlSessionTemplate.class);
                    Map<String, SqlSessionTemplate> beansOfType2 = context.getBeansOfType(SqlSessionTemplate.class);

                    assertThat(beansOfType2.size()).isEqualTo(2);

                });
    }


    @Test
    public void test3() {
        this.contextRunner
                .withPropertyValues("spring.application.name=grus-demo", "grus.db.read-write-separation=true")
                .run(context -> {
                    Object masterDataSource = context.getBean("masterDataSource");
                    assertThat(masterDataSource).isInstanceOf(HikariDataSource.class);

                    Object slaveDataSource = context.getBean("slaveDataSource");
                    assertThat(slaveDataSource).isInstanceOf(HikariDataSource.class);

                    Map<String, HikariDataSource> beansOfType = context.getBeansOfType(HikariDataSource.class);

                    assertThat(beansOfType.size()).isEqualTo(2);

                    Object writeSqlSessionTemplate = context.getBean("writeSqlSessionTemplate");
                    assertThat(writeSqlSessionTemplate).isInstanceOf(SqlSessionTemplate.class);
                    Map<String, SqlSessionTemplate> beansOfType2 = context.getBeansOfType(SqlSessionTemplate.class);

                    assertThat(beansOfType2.size()).isEqualTo(2);

                });
    }


}
