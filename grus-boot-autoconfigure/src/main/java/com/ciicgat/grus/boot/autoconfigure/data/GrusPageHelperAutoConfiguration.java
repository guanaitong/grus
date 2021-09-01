/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;

import com.ciicgat.sdk.util.bean.BeanMapUtil;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Mybatis分页插件PageHelper（https://github.com/pagehelper/Mybatis-PageHelper）
 *
 * @author wanchongyang
 * @date 2019-05-08 16:40
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = PageHelperProperties.PAGE_HELPER_PREFIX, value = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class, PageInterceptor.class})
@EnableConfigurationProperties(PageHelperProperties.class)
@AutoConfigureBefore(GrusMybatisAutoConfiguration.class)
public class GrusPageHelperAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public PageInterceptor pageInterceptor(PageHelperProperties pageHelperProperties) {
        PageInterceptor interceptor = new PageInterceptor();
        Map<String, Object> map = BeanMapUtil.bean2Map(pageHelperProperties);
        Properties properties = new Properties();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (Objects.nonNull(value) && !"".equals(value)) {
                properties.setProperty(entry.getKey(), String.valueOf(value));
            }
        }
        interceptor.setProperties(properties);
        return interceptor;
    }
}
