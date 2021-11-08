/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import com.ciicgat.grus.boot.autoconfigure.core.GrusCoreContextInitializer;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Map;

import static com.ciicgat.grus.boot.autoconfigure.test.TestConstants.TEST_APP_PAIRS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by August.Zhou on 2019-04-16 13:25.
 */
public class GrusDataAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new GrusCoreContextInitializer())
            .withUserConfiguration(GrusDataAutoConfiguration.class, GrusMybatisAutoConfiguration.class);

    @Test
    public void testReadWriteSeparationFalse() {
        contextRunner
                .withPropertyValues(TEST_APP_PAIRS)
                .run(context -> {
                    Object masterDataSource = context.getBean("masterDataSource");
                    assertThat(masterDataSource).isInstanceOf(HikariDataSource.class);

                    Map<String, HikariDataSource> beansOfType = context.getBeansOfType(HikariDataSource.class);

                    assertThat(beansOfType.size()).isEqualTo(1);

                    Object writeSqlSessionTemplate = context.getBean("writeSqlSessionTemplate");
                    assertThat(writeSqlSessionTemplate).isInstanceOf(SqlSessionTemplate.class);
                    Map<String, SqlSessionTemplate> beansOfType2 = context.getBeansOfType(SqlSessionTemplate.class);

                    assertThat(beansOfType2.size()).isEqualTo(1);

                    Object writeSqlSessionFactory = context.getBean("writeSqlSessionFactory");
                    assertThat(writeSqlSessionFactory).isInstanceOf(SqlSessionFactory.class);
                    Map<String, SqlSessionFactory> beansOfType3 = context.getBeansOfType(SqlSessionFactory.class);
                    assertThat(beansOfType3.size()).isEqualTo(1);

                });
    }


    @Test
    public void testReadWriteSeparationTrue() {
        this.contextRunner
                .withPropertyValues(TEST_APP_PAIRS, "grus.db.readWriteSeparation=true")
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
    public void testDbProperties() {
        contextRunner
                .withPropertyValues(TEST_APP_PAIRS, "grus.db.jdbcParams.useAffectedRows=true", "grus.db.dataSourceExtParams.minimumIdle=5")
                .run(context -> {
                    Object masterDataSource = context.getBean("masterDataSource");
                    assertThat(masterDataSource).isInstanceOf(HikariDataSource.class);

                    Map<String, HikariDataSource> beansOfType = context.getBeansOfType(HikariDataSource.class);

                    assertThat(beansOfType.size()).isEqualTo(1);

                    Object writeSqlSessionTemplate = context.getBean("writeSqlSessionTemplate");
                    assertThat(writeSqlSessionTemplate).isInstanceOf(SqlSessionTemplate.class);
                    Map<String, SqlSessionTemplate> beansOfType2 = context.getBeansOfType(SqlSessionTemplate.class);

                    assertThat(beansOfType2.size()).isEqualTo(1);

                    Object writeSqlSessionFactory = context.getBean("writeSqlSessionFactory");
                    assertThat(writeSqlSessionFactory).isInstanceOf(SqlSessionFactory.class);
                    Map<String, SqlSessionFactory> beansOfType3 = context.getBeansOfType(SqlSessionFactory.class);
                    assertThat(beansOfType3.size()).isEqualTo(1);

                });
    }


}
