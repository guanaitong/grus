/*
 * Copyright 2007-2023, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.data;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

/**
 * Created by August.Zhou on 2019-04-03 14:58.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties({MybatisProperties.class, DbProperties.class})
@AutoConfigureAfter(GrusDataAutoConfiguration.class)
@AutoConfigureBefore({MybatisAutoConfiguration.class})
@Import(GrusInterceptors.class)
public class GrusMybatisAutoConfiguration implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrusMybatisAutoConfiguration.class);

    private final MybatisProperties properties;

    private final Interceptor[] interceptors;

    private final ResourceLoader resourceLoader;

    private final DatabaseIdProvider databaseIdProvider;

    private final List<ConfigurationCustomizer> configurationCustomizers;


    public GrusMybatisAutoConfiguration(MybatisProperties properties,
                                        ObjectProvider<Interceptor[]> interceptorsProvider,
                                        ResourceLoader resourceLoader,
                                        ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                        ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        this.properties = properties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
    }


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "grus.db", value = "read-write-separation", havingValue = "false", matchIfMissing = true)
    public class DisableReadWriteSeparationAutoConfiguration {

        @Bean(name = {"sqlSessionFactory", "writeSqlSessionFactory"})
        @ConditionalOnMissingBean(name = "writeSqlSessionFactory")
        public SqlSessionFactory writeSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource) throws Exception {
            return createFactory(masterDataSource);
        }

        @Bean(name = {"sqlSessionTemplate", "writeSqlSessionTemplate"})
        @ConditionalOnMissingBean(name = "writeSqlSessionTemplate")
        public SqlSessionTemplate writeSqlSessionTemplate(@Qualifier("writeSqlSessionFactory") SqlSessionFactory writeSqlSessionFactory) {
            return new SqlSessionTemplate(writeSqlSessionFactory);
        }


    }


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "grus.db", value = "read-write-separation", havingValue = "true", matchIfMissing = false)
    public class EnableReadWriteSeparationAutoConfiguration {


        @Bean(name = {"sqlSessionFactory", "writeSqlSessionFactory"})
        @Primary
        @ConditionalOnMissingBean(name = {"writeSqlSessionFactory"})
        public SqlSessionFactory writeSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource) throws Exception {
            return createFactory(masterDataSource);
        }

        @Bean(name = {"sqlSessionTemplate", "writeSqlSessionTemplate"})
        @Primary
        @ConditionalOnMissingBean(name = "writeSqlSessionTemplate")
        public SqlSessionTemplate writeSqlSessionTemplate(@Qualifier("writeSqlSessionFactory") SqlSessionFactory writeSqlSessionFactory) {
            return new SqlSessionTemplate(writeSqlSessionFactory);
        }


        @Bean
        @ConditionalOnMissingBean(name = "readSqlSessionFactory")
        public SqlSessionFactory readSqlSessionFactory(@Qualifier("slaveDataSource") DataSource slaveDataSource) throws Exception {
            return createFactory(slaveDataSource);
        }

        @Bean
        @ConditionalOnMissingBean(name = "readSqlSessionTemplate")
        public SqlSessionTemplate readSqlSessionTemplate(@Qualifier("readSqlSessionFactory") SqlSessionFactory readSqlSessionFactory) throws Exception {
            return new SqlSessionTemplate(readSqlSessionFactory);
        }


    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("masterDataSource") DataSource masterDataSource) {
        return new GrusDataSourceTransactionManager(masterDataSource);
    }


    public SqlSessionFactory createFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        applyConfiguration(factory);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (this.properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        Resource[] resources = this.properties.resolveMapperLocations();
        if (!ObjectUtils.isEmpty(resources)) {
            factory.setMapperLocations(resources);
        }

        return factory.getObject();
    }


    private void applyConfiguration(SqlSessionFactoryBean factory) {
        org.apache.ibatis.session.Configuration configuration = null;
        org.apache.ibatis.session.Configuration orinalConfiguration = this.properties.getConfiguration();
        if (orinalConfiguration == null) {
            if (StringUtils.hasText(this.properties.getConfigLocation())) {
                Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
                try {
                    XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(resource.getInputStream(), null, this.properties.getConfigurationProperties());
                    configuration = xmlConfigBuilder.getConfiguration();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                configuration = new org.apache.ibatis.session.Configuration();
                this.properties.setConfiguration(configuration);
            }
        } else {
            // 这里基于orinalConfiguration克隆一个新的configuration。解决不同数据源共享同一个Configuration的问题
            // Configuration里面有一个environment，environment里有datasource。
            // 如果共用同一个Configuration，那么也意味着多数据情况下只有最后一个datasource生效。显然不符合预期
            configuration = new org.apache.ibatis.session.Configuration();
            configuration.setSafeRowBoundsEnabled(orinalConfiguration.isSafeRowBoundsEnabled());
            configuration.setSafeResultHandlerEnabled(orinalConfiguration.isSafeResultHandlerEnabled());
            configuration.setMapUnderscoreToCamelCase(orinalConfiguration.isMapUnderscoreToCamelCase());
            configuration.setAggressiveLazyLoading(orinalConfiguration.isAggressiveLazyLoading());
            configuration.setMultipleResultSetsEnabled(orinalConfiguration.isMultipleResultSetsEnabled());
            configuration.setUseGeneratedKeys(orinalConfiguration.isUseGeneratedKeys());
            configuration.setUseColumnLabel(orinalConfiguration.isUseColumnLabel());
            configuration.setCacheEnabled(orinalConfiguration.isCacheEnabled());
            configuration.setCallSettersOnNulls(orinalConfiguration.isCallSettersOnNulls());
            configuration.setUseActualParamName(orinalConfiguration.isUseActualParamName());
            configuration.setReturnInstanceForEmptyRow(orinalConfiguration.isReturnInstanceForEmptyRow());
            configuration.setShrinkWhitespacesInSql(orinalConfiguration.isShrinkWhitespacesInSql());
            configuration.setLogPrefix(orinalConfiguration.getLogPrefix());
            configuration.setLogImpl(orinalConfiguration.getLogImpl());
            configuration.setVfsImpl(orinalConfiguration.getVfsImpl());
            configuration.setDefaultSqlProviderType(orinalConfiguration.getDefaultSqlProviderType());
            configuration.setLocalCacheScope(orinalConfiguration.getLocalCacheScope());
            configuration.setJdbcTypeForNull(orinalConfiguration.getJdbcTypeForNull());
            configuration.setLazyLoadTriggerMethods(orinalConfiguration.getLazyLoadTriggerMethods());
            configuration.setDefaultStatementTimeout(orinalConfiguration.getDefaultStatementTimeout());
            configuration.setDefaultFetchSize(orinalConfiguration.getDefaultFetchSize());
            configuration.setDefaultResultSetType(orinalConfiguration.getDefaultResultSetType());
            configuration.setDefaultExecutorType(orinalConfiguration.getDefaultExecutorType());
            configuration.setAutoMappingBehavior(orinalConfiguration.getAutoMappingBehavior());
            configuration.setAutoMappingUnknownColumnBehavior(orinalConfiguration.getAutoMappingUnknownColumnBehavior());
            configuration.setVariables(orinalConfiguration.getVariables());
            configuration.setReflectorFactory(orinalConfiguration.getReflectorFactory());
            configuration.setObjectFactory(orinalConfiguration.getObjectFactory());
            configuration.setObjectWrapperFactory(orinalConfiguration.getObjectWrapperFactory());
            configuration.setLazyLoadingEnabled(orinalConfiguration.isLazyLoadingEnabled());
            configuration.setProxyFactory(orinalConfiguration.getProxyFactory());
            configuration.setConfigurationFactory(orinalConfiguration.getConfigurationFactory());
        }


        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
    }

    @Override
    public void afterPropertiesSet() {
        checkConfigFileExists();
    }

    private void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(), "Cannot find config location: " + resource
                    + " (please add config file or check your Mybatis configuration)");
        }
    }


    /**
     * This will just scan the same base package as Spring Boot does. If you want
     * more power, you can explicitly use
     * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed
     * mappers working correctly, out-of-the-box, similar to using Spring Data JPA
     * repositories.
     */
    public static class AutoConfiguredMapperScannerRegistrar
            implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

        private BeanFactory beanFactory;

        private ResourceLoader resourceLoader;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

            if (!AutoConfigurationPackages.has(this.beanFactory)) {
                LOGGER.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
                return;
            }

            LOGGER.debug("Searching for mappers annotated with @Mapper");

            List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
            if (LOGGER.isDebugEnabled()) {
                packages.forEach(pkg -> LOGGER.debug("Using auto-configuration base package '{}'", pkg));
            }

            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }
            scanner.setAnnotationClass(Mapper.class);
            scanner.registerFilters();
            scanner.doScan(StringUtils.toStringArray(packages));

        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }
    }

    /**
     * {@link org.mybatis.spring.annotation.MapperScan} ultimately ends up
     * creating instances of {@link MapperFactoryBean}. If
     * {@link org.mybatis.spring.annotation.MapperScan} is used then this
     * auto-configuration is not needed. If it is _not_ used, however, then this
     * will bring in a bean registrar and automatically register components based
     * on the same component-scanning path as Spring Boot itself.
     */
    @org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
    @Import({AutoConfiguredMapperScannerRegistrar.class})
    @ConditionalOnMissingBean(MapperFactoryBean.class)
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {

        @Override
        public void afterPropertiesSet() {
            LOGGER.debug("No {} found.", MapperFactoryBean.class.getName());
        }
    }
}
