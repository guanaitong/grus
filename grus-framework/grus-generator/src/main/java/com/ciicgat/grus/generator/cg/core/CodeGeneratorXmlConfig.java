/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.generator.cg.core;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 代码生成配置
 *
 * @author Clive Yuan
 * @date 2020/09/07
 */
public class CodeGeneratorXmlConfig implements Serializable {

    private BaseConfig baseConfig;
    private JdbcConnection jdbcConnection;
    private MapperGeneratorConfig javaModelGenerator;
    private MapperGeneratorConfig sqlMapGenerator;
    private MapperGeneratorConfig javaClientGenerator;
    private MapperGeneratorConfig serviceGenerator;
    private MapperGeneratorConfig controllerGenerator;
    private MapperGeneratorConfig dtoGenerator;
    private List<Table> tables;
    private List<String> fixedColumns;
    private List<Field> fixedField;

    public BaseConfig getBaseConfig() {
        return baseConfig;
    }

    public CodeGeneratorXmlConfig setBaseConfig(BaseConfig baseConfig) {
        this.baseConfig = baseConfig;
        return this;
    }

    public JdbcConnection getJdbcConnection() {
        return jdbcConnection;
    }

    public CodeGeneratorXmlConfig setJdbcConnection(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
        return this;
    }

    public MapperGeneratorConfig getJavaModelGenerator() {
        return javaModelGenerator;
    }

    public CodeGeneratorXmlConfig setJavaModelGenerator(MapperGeneratorConfig javaModelGenerator) {
        this.javaModelGenerator = javaModelGenerator;
        return this;
    }

    public MapperGeneratorConfig getSqlMapGenerator() {
        return sqlMapGenerator;
    }

    public CodeGeneratorXmlConfig setSqlMapGenerator(MapperGeneratorConfig sqlMapGenerator) {
        this.sqlMapGenerator = sqlMapGenerator;
        return this;
    }

    public MapperGeneratorConfig getJavaClientGenerator() {
        return javaClientGenerator;
    }

    public CodeGeneratorXmlConfig setJavaClientGenerator(MapperGeneratorConfig javaClientGenerator) {
        this.javaClientGenerator = javaClientGenerator;
        return this;
    }

    public MapperGeneratorConfig getServiceGenerator() {
        return serviceGenerator;
    }

    public CodeGeneratorXmlConfig setServiceGenerator(MapperGeneratorConfig serviceGenerator) {
        this.serviceGenerator = serviceGenerator;
        return this;
    }

    public MapperGeneratorConfig getControllerGenerator() {
        return controllerGenerator;
    }

    public CodeGeneratorXmlConfig setControllerGenerator(MapperGeneratorConfig controllerGenerator) {
        this.controllerGenerator = controllerGenerator;
        return this;
    }

    public MapperGeneratorConfig getDtoGenerator() {
        return dtoGenerator;
    }

    public CodeGeneratorXmlConfig setDtoGenerator(MapperGeneratorConfig dtoGenerator) {
        this.dtoGenerator = dtoGenerator;
        return this;
    }

    public List<Table> getTables() {
        return tables;
    }

    public CodeGeneratorXmlConfig setTables(List<Table> tables) {
        this.tables = tables;
        return this;
    }

    public List<String> getFixedColumns() {
        return fixedColumns;
    }

    public CodeGeneratorXmlConfig setFixedColumns(List<String> fixedColumns) {
        this.fixedColumns = fixedColumns;
        return this;
    }

    public List<Field> getFixedField() {
        return fixedField;
    }

    public CodeGeneratorXmlConfig setFixedField(List<Field> fixedField) {
        this.fixedField = fixedField;
        return this;
    }

    public static class BaseConfig {
        // 启用Lombok
        private boolean enableLombok;
        // 启用swagger
        private boolean enableSwagger;
        // 启用验证
        private boolean enableValidation;
        // 启用读写分离
        private boolean enableReadWriteSeparation;
        // 禁用更新MapperXml字段
        private boolean disableUpdatingMapperXml;
        // 启用@Mapper注解
        private boolean enableMapperAnnotation;

        public boolean isEnableLombok() {
            return enableLombok;
        }

        public BaseConfig setEnableLombok(boolean enableLombok) {
            this.enableLombok = enableLombok;
            return this;
        }

        public boolean isEnableSwagger() {
            return enableSwagger;
        }

        public BaseConfig setEnableSwagger(boolean enableSwagger) {
            this.enableSwagger = enableSwagger;
            return this;
        }

        public boolean isEnableValidation() {
            return enableValidation;
        }

        public BaseConfig setEnableValidation(boolean enableValidation) {
            this.enableValidation = enableValidation;
            return this;
        }

        public boolean isEnableReadWriteSeparation() {
            return enableReadWriteSeparation;
        }

        public BaseConfig setEnableReadWriteSeparation(boolean enableReadWriteSeparation) {
            this.enableReadWriteSeparation = enableReadWriteSeparation;
            return this;
        }

        public boolean isDisableUpdatingMapperXml() {
            return disableUpdatingMapperXml;
        }

        public BaseConfig setDisableUpdatingMapperXml(boolean disableUpdatingMapperXml) {
            this.disableUpdatingMapperXml = disableUpdatingMapperXml;
            return this;
        }

        public boolean isEnableMapperAnnotation() {
            return enableMapperAnnotation;
        }

        public BaseConfig setEnableMapperAnnotation(boolean enableMapperAnnotation) {
            this.enableMapperAnnotation = enableMapperAnnotation;
            return this;
        }
    }

    public static class JdbcConnection {
        private String driverClass;
        private String connectionURL;
        private String username;
        private String password;

        public String getDriverClass() {
            return driverClass;
        }

        public JdbcConnection setDriverClass(String driverClass) {
            this.driverClass = driverClass;
            return this;
        }

        public String getConnectionURL() {
            return connectionURL;
        }

        public JdbcConnection setConnectionURL(String connectionURL) {
            this.connectionURL = connectionURL;
            return this;
        }

        public String getUsername() {
            return username;
        }

        public JdbcConnection setUsername(String username) {
            this.username = username;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public JdbcConnection setPassword(String password) {
            this.password = password;
            return this;
        }
    }

    public static class MapperGeneratorConfig {
        /**
         * 是否禁用
         */
        private boolean disabled;
        /**
         * 模板路径(相对resource路径)
         */
        private String templatePath;
        /**
         * 代码路径(相对root路径)
         */
        private String codePath;
        /**
         * 目标包名
         */
        private String targetPackage;
        /**
         * 后缀
         */
        private String suffix;

        // 以下为内部字段,不从xml中读取
        /**
         * 模块名
         */
        private String moduleName;

        public boolean isDisabled() {
            return disabled;
        }

        public MapperGeneratorConfig setDisabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public String getTemplatePath() {
            return templatePath;
        }

        public MapperGeneratorConfig setTemplatePath(String templatePath) {
            this.templatePath = templatePath;
            return this;
        }

        public String getCodePath() {
            return codePath;
        }

        public MapperGeneratorConfig setCodePath(String codePath) {
            this.codePath = codePath;
            return this;
        }

        public String getTargetPackage() {
            return targetPackage;
        }

        public MapperGeneratorConfig setTargetPackage(String targetPackage) {
            this.targetPackage = targetPackage;
            return this;
        }

        public String getSuffix() {
            return suffix;
        }

        public MapperGeneratorConfig setSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public String getModuleName() {
            return moduleName;
        }

        public MapperGeneratorConfig setModuleName(String moduleName) {
            this.moduleName = moduleName;
            return this;
        }
    }

    public static class Table {
        private String tableName;
        private String entityObjectName;
        private Set<String> ignoreColumns;
        private String entityObjectSuffix;

        public String getTableName() {
            return tableName;
        }

        public Table setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public String getEntityObjectName() {
            return entityObjectName;
        }

        public Table setEntityObjectName(String entityObjectName) {
            this.entityObjectName = entityObjectName;
            return this;
        }

        public Set<String> getIgnoreColumns() {
            return ignoreColumns;
        }

        public Table setIgnoreColumns(Set<String> ignoreColumns) {
            this.ignoreColumns = ignoreColumns;
            return this;
        }

        public String getEntityObjectSuffix() {
            return entityObjectSuffix;
        }

        public Table setEntityObjectSuffix(String entityObjectSuffix) {
            this.entityObjectSuffix = entityObjectSuffix;
            return this;
        }
    }

}


